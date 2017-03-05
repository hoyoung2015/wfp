import sys
import os

curPath = os.path.abspath(os.path.dirname(__file__))
rootPath = os.path.split(curPath)[0]
print(rootPath)
sys.path.append(rootPath)


from common.downloader import download
from common.mongo import mongo_cli
import re
import hashlib
from urllib.error import HTTPError
from urllib.error import URLError
from common import log
import logging
import queue
import time





def get_collection_names(db_name):
    db = mongo_cli.get_database(db_name)
    return [name for name in db.collection_names() if re.match('\d{6}', name)]


def split_url(url):
    i = url.rindex('.')
    sha1 = hashlib.sha1()
    sha1.update(url[:i].encode('utf-8'))
    return sha1.hexdigest(), url[i:]


def write_error(message):
    with open('download_error.txt', 'a') as f:
        f.write('%s\t%s\t%s\t%s\n' % message)
        f.close()


def get_proxies(file=''):
    q = queue.Queue()
    with open(file) as f:
        for line in f.readlines():
            line = line.strip().replace('\n', '')
            if line == '' or line.startswith('#'):
                continue
            sp = line.split(',')
            q.put({
                'http': 'http://%s:%s@%s:%s' % tuple(sp)
            })
        f.close()
    return q

if __name__ == '__main__':
    proxy_queue = get_proxies('../../wfp-spider/src/main/resources/proxy.txt')

    logger = log.get_logger('doc_downloader', 'downloader.log', level=logging.INFO)
    db_name = 'wfp_com_page'
    db = mongo_cli.get_database(db_name)
    for collection_name in get_collection_names(db_name):
        filter = {
            'url': {
                '$regex': '\.(pdf|PDF|doc|DOC|docx|DOCX)$'
            }
        }
        collection = db.get_collection(collection_name)
        total = collection.count(filter)
        if total == 0:
            continue
        save_dir = '/Users/baidu/tmp/downloader/' + collection_name
        if os.path.exists(save_dir) is False:
            os.mkdir(save_dir)
        counter = 0
        for d in collection.find(filter, projection=['url']):
            url = d['url']
            name, ext = split_url(url)
            landing_file_path = save_dir + '/' + name + ext
            counter += 1
            if os.path.exists(landing_file_path):
                continue
            logger.info('%s\t%d/%d\t%s\t%s' % (collection_name, counter, total, name, url))
            try:
                download(url, landing_file_path, blocks=5)
                time.sleep(2)
            except HTTPError as e:
                logger.warn(e)
                if e.code == 404:
                    collection.delete_one({'_id': d['_id']})
                elif e.code == 503:
                    write_error((collection_name, d['_id'], url, e))
                    break
                else:
                    write_error((collection_name, d['_id'], url, e))
            except URLError as e:
                # 断网
                logger.warn(e)
                exit(-1)

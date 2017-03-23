import sys
import os
import re
import sqlite3
import shutil

curPath = os.path.abspath(os.path.dirname(__file__))
rootPath = os.path.split(curPath)[0]
print(rootPath)
sys.path.append(rootPath)

from common.mongo import mongo_cli
import re
import hashlib
from common import log
import logging
from common.xunleidata import *
import random


def a1(taskid, DisplayUrl):
    P2spTask(
        Id=taskid,
        ResourceUsageStrategy=0,
        ResourceReportStrategy=0,
        Cookie='',
        AccountNeeded=0,
        UserName='',
        Password='',
        UseOriginResourceOnly=0,
        OriginResourceSupportRange=1,
        ReceiveOriginResourceSize=0,
        OriginResourceRetryInterval=-1,
        OriginResourceRetryTimes=-1,
        OriginResourceThreadCount=5,
        DownloadSpeedLimit=0,
        FileNameFixed=0,
        HttpRequestHeader=None,
        VideoHeadFirstTime=-1,
        VideoHeadFirstStatus=0,
        ResourceQuerySize=0,
        DisplayUrl=DisplayUrl,
        UserAgent=''
    )


# P2spTask._connection.debug = True


def a2(taskid, Url, save_path, Name):
    TaskBase(
        TaskId=taskid,
        Type=1,
        Status=7,  # 3 排队. 5 开始. 7暂停. 8完成
        StatusChangeTime=0,
        # StatusChangeTime = 23284358513164200,
        SavePath=save_path,
        TotalReceiveSize=0,
        TotalSendSize=0,
        TotalReceiveValidSize=0,
        TotalUploadSize=0,
        CreationTime=0,
        # CreationTime = 23284358328614900,
        FileCreated=0,
        CompletionTime=0,
        DownloadingPeriod=0,
        RemovingToRecycleTime=0,
        FailureErrorCode=0,
        Url=Url,
        ReferenceUrl='',
        ResourceSize=0,
        Name=Name,
        Cid=None,
        Gcid=None,
        Description='',  # 备注名称
        CategoryId=-1,
        ResourceQueryCid=None,
        CreationRequestType=0,
        StartMode=0,
        NamingType=1,
        StatisticsReferenceUrl='',
        UserRead=0,
        FileSafetyFlag=0,
        Playable=0,
        BlockInfo=None,
        OpenOnComplete=0,
        SpecialType=-1,
        Proxy=None,
        OriginReceiveSize=0,
        P2pReceiveSize=0,
        P2sReceiveSize=0,
        OfflineReceiveSize=0,
        VipReceiveSize=0,
        VipResourceEnableNecessary=0,
        ConsumedVipSize=0,
        Forbidden=0,
        OptionalChannelDataSize=None,
        OwnerProductId=0,
        UserData=None,
        UrlCodePage=-1,
        ReferenceUrlCodePage=-1,
        StatisticsReferenceUrlCodePage=-1,
        GroupTaskId=-1,
        DownloadSubTask=0,
        TagValue=0,
        InnerNatReceiveSize=0,
        AdditionFlag=0, )


def read_failed_url(db_file):
    failed_task = set()
    conn = sqlite3.connect(db_file)
    cursor = conn.execute("""
    SELECT a.DisplayUrl FROM P2spTask a
    LEFT JOIN TaskBase b ON a.TaskId=b.TaskId
    WHERE b.Status!=8
    """)
    for row in cursor:
        failed_task.add(row[0])
    conn.close()
    return failed_task


def get_failed_task(downlist_dir):
    failed_task = set()
    for downlist_file in os.listdir(downlist_dir):
        if not re.match('.+\.downlist', downlist_file):
            continue
        print(downlist_file)
        with open(downlist_dir + '/' + downlist_file) as f:
            for line in f.readlines():
                line = line.strip().replace('\n', '')
                if line == '':
                    continue
                failed_task.add(line)
            f.close()
    return failed_task


def get_collection_names(db_name):
    db = mongo_cli.get_database(db_name)
    return [name for name in db.collection_names() if re.match('^\d{6}$', name)]


def split_url(url, filename):
    sha1 = hashlib.sha1()
    if filename is not None and re.match('.+\.(pdf|PDF|doc|DOC|docx|DOCX)$', filename):
        e = filename[filename.rindex('.'):]
        sha1.update(url.encode('utf-8'))
    elif re.match('.+\.(pdf|PDF|doc|DOC|docx|DOCX)$', url):
        e = url[url.rindex('.'):]
        sha1.update(url[:url.rindex('.')].encode('utf-8'))
    else:
        e = None

    return sha1.hexdigest(), e


if __name__ == '__main__':

    if os.path.exists('TaskDb.dat'):
        os.remove('TaskDb.dat')
    # exit(0)
    shutil.copyfile('/home/hoyoung/tmp/TaskDb.dat', 'TaskDb.dat')

    # failed_urls_set = get_failed_task('/media/hoyoung/win7 home/compage/000000')
    failed_urls_set = read_failed_url(
        '/media/hoyoung/win7 home/Program Files/Thunder Network/Thunder9/Profiles/TaskDb.dat')
    taskid = int(random.randint(10000009937, 99999819937))
    logger = log.get_logger('pdf_export', 'pdf_export.log', level=logging.INFO)
    db_name = 'wfp_com_page'
    db = mongo_cli.get_database(db_name)
    batch_num = 0
    for collection_name in get_collection_names(db_name):
        filter = {
            '$and': [
                {
                    'xunlei': {'$exists': False}
                }, {
                    '$or': [{'url': {
                        '$regex': '\.(pdf|PDF|doc|DOC|docx|DOCX)$'
                    }}, {'contentType': {
                        '$in': ['pdf', 'msword']
                    }}]
                }
            ]
        }
        collection = db.get_collection(collection_name)
        total = collection.count(filter)
        # print(total)
        # exit(0)
        if total == 0:
            continue
        logger.info('%s\t%d' % (collection_name, total))
        save_dir = 'D:\\compage\\' + collection_name + '\\'
        exists_dir = '/media/hoyoung/win7 home/compage/' + collection_name + '/'
        # if collection_name == '000060':
        #     continue
        cnt = 0
        for d in collection.find(filter, projection=['url', 'contentType', 'filename']):
            url = d['url']
            contentType = d['contentType']
            # 如果在失败列表中,删除
            if url in failed_urls_set:
                collection.delete_one({'_id': d['_id']})
                continue

            if 'filename' in d:
                filename = d['filename']
            else:
                filename = None
            name, ext = split_url(url, filename)
            if ext is None:
                if contentType == 'pdf':
                    ext = '.pdf'
                elif contentType == 'msword':
                    ext = '.doc'
                else:
                    collection.delete_one({'_id': d['_id']})
                    continue
            # 判断是否已经下载
            if os.path.exists(exists_dir + name + ext.lower()) or os.path.exists(exists_dir + name + ext):
                collection.update_one({'_id': d['_id']}, {'$set': {'xunlei': 1}})
                continue
            taskid += 1
            fail_num = 0
            cnt += 1
            print('\r%s\t%d/%d' % (collection_name, cnt, total), end='')
            try:
                a1(taskid, url)
            except:
                fail_num += 1
            try:
                a2(taskid, url, save_dir, name + ext)
            except:
                fail_num += 1
            batch_num += 1
        # break
        if batch_num > 2000:
            break

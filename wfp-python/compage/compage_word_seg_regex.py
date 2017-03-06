from common.mongo import mongo_cli
from common import log
import logging
import re
import xlrd


def get_collection_names(db_name):
    db = mongo_cli.get_database(db_name)
    return [name for name in db.collection_names() if re.match('^\d{6}$', name)]


def read_regex_from_excel(filename):
    data = xlrd.open_workbook(filename)  # 打开xls文件
    table = data.sheets()[0]  # 打开第一张表
    nrows = table.nrows  # 获取表的行数
    m = {}
    for i in range(nrows):  # 循环逐行打印
        word = table.row_values(i)[4]
        word = word.replace(',', '|')
        m[table.row_values(i)[2]] = word
    return m


def read_stopwords(filename):
    words = set()
    with open(filename) as f:
        for s in f.readlines():
            s = s.strip().replace('\n', '')
            words.add(s)
        f.close()
    return words


if __name__ == '__main__':
    logger = log.get_logger(name='word_find', filename='word_find.log', level=logging.INFO)
    dbname = 'wfp_com_page'
    db = mongo_cli.get_database(dbname)
    stopwords = read_stopwords('../data/stopwords.txt')
    word_vars = read_regex_from_excel('../data/word_meta.xlsx')

    for collection_name in get_collection_names(dbname):
        collection = db.get_collection(collection_name)
        filter = {
            'content': {
                '$exists': True
            }
        }
        count = collection.count(filter)
        logger.info('start to process collection %s, count %d', collection_name, count)
        for doc in collection.find(filter, projection=['content']):
            content = doc['content']
            # content = '废品回收是我们的义务，灰渣采集。废品'
            good = 0
            tmp_vars = {}
            for k, v in word_vars.items():
                if v == '':
                    continue
                wdl = re.findall('(' + v + ')', content)
                if len(wdl) > 0:
                    good = 1
                    word_map = {}
                    for x in wdl:
                        if x in word_map:
                            word_map[x] += 1
                        else:
                            word_map[x] = 1
                    tmp_vars[k] = word_map

            update_data = {
                'good': good
            }
            if good == 1:
                update_data['vars'] = tmp_vars
            collection.update_one({'_id': doc['_id']}, {'$set': update_data})

        # break  # collection_name

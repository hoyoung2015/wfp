from common.mongo import mongo_cli
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
        m[str(int(table.row_values(i)[1]))] = word
    return m


def join_keyword(map1={}, map2={}):
    for _k, _v in map2.items():
        for _m, _n in _v.items():
            if _m in map1[_k]:
                # 该指标下，关键词存在
                map1[_k][_m] += map2[_k][_m]
            else:
                map1[_k][_m] = map2[_k][_m]
    return map1


if __name__ == '__main__':
    dbname = 'wfp_com_page'
    db = mongo_cli.get_database(dbname)
    word_vars_collection = mongo_cli.get_database('wfp').get_collection('word_vars')
    word_vars = read_regex_from_excel('../data/word_meta.xlsx')
    num = 0
    for collection_name in get_collection_names(dbname):
        num += 1
        collection = db.get_collection(collection_name)
        filters = {}
        total = collection.count(filters)
        word_result = {}
        for var_num in word_vars.keys():
            word_result[var_num] = {}
        cnt = 0
        green_doc_num = 0
        for doc in collection.find(filters, projection=['content']):
            cnt += 1
            content = doc['content']
            content = content.strip().replace('\n', '').replace(' ', '')
            # content = '废品回收是我们的义务，灰渣采集。废品'
            tmp_vars = {}
            for var_num, regex in word_vars.items():
                if regex == '':
                    continue
                wdl = re.findall('(' + regex + ')', content)
                if len(wdl) > 0:
                    word_map = {}
                    for x in wdl:
                        if x in word_map:
                            word_map[x] += 1
                        else:
                            word_map[x] = 1
                    tmp_vars[var_num] = word_map
            if len(tmp_vars.keys()) > 0:
                green_doc_num += 1
            join_keyword(word_result, tmp_vars)
            print('\r%d\t%s\t%d/%d\t%d' % (num, collection_name, cnt, total, green_doc_num), end='')
        print()
        # print(word_result)
        word_vars_collection.update_one({'stockCode': collection_name},
                                        {'$set': {'vars': word_result, 'pageSize': total,
                                                  'greenDocNum': green_doc_num}}, upsert=True)
        # break

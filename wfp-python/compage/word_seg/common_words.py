import pandas as pd
import re
from common.mongo import mongo_cli
import os


def get_collection_names(db_name):
    db = mongo_cli.get_database(db_name)
    return [name for name in db.collection_names() if re.match('^\d{6}$', name)]


def read_keywords(file=''):
    _rs_set = set()
    with open(file) as f:
        for line in f.readlines():
            line = line.strip().replace('\n', '')
            if line == '' or line.startswith('word\t'):
                continue
            _rs_set.add(line.split('\t')[0])
    return _rs_set


keywords = read_keywords('/Users/baidu/workspace/wfp/wfp-python/zrbg/pdfminer/final_word.txt')

# keywords = ['a', 'b', 'c', 'd']


db_name = 'wfp_com_page'
stocks = get_collection_names(db_name)
i = 0
for stock in stocks:
    if os.path.exists('/Users/baidu/tmp/common_words/' + stock + '.xlsx'):
        continue

    i += 1
    collection = mongo_cli.get_database(db_name).get_collection(stock)
    j = 0
    total = collection.count()
    df = pd.DataFrame(columns=keywords, index=keywords).fillna(value=0)
    for d in collection.find({}, projection={'words': 1}):
        j += 1
        m = d['words']
        if len(m.keys()) == 0:
            continue
        for x in keywords:
            if x not in m:
                continue
            for y in keywords:
                if x == y:
                    break
                if x in m and y in m:
                    df.loc[x, y] += min(m[x], m[y])
        print('\r%d\t%s\t%d/%d' % (i, stock, j, total), end='')
    print()
    df.to_excel('/Users/baidu/tmp/common_words/' + stock + '.xlsx', sheet_name='Sheet1')
    # break

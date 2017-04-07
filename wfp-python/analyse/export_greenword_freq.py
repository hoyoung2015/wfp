from lib.wfp_com_page import get_stocks
from common.file_utils import read_lines
from common.mongo import mongo_cli
import pandas as pd

stocks = get_stocks()
lines = read_lines('../zrbg/words/words_bak.txt')
keywords = set([x.split('\t')[0] for x in lines])

db_name = 'wfp_com_page'
db = mongo_cli.get_database(db_name)

data_array = []
i = 0
for stock in stocks:
    i += 1
    collection = db.get_collection(stock)
    total = collection.count()
    total_green_words = 0
    total_words = 0
    cnt = 0
    for d in collection.find():
        cnt += 1
        words = d['words']
        total_words += sum(words.values())
        for w, c in words.items():
            if w in keywords:
                total_green_words += c
        print('\r%d-%s\t%d/%d' % (i, stock, cnt, total), end='')
    print()
    data_array.append([stock, total_green_words, total_words])
    # break

df = pd.DataFrame(data_array, columns=['stockCode', 'greenWords', 'totalWords'])
df.to_csv('green_words_count.csv', index=False)

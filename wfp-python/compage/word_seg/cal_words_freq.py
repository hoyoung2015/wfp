from common.mongo import mongo_cli
import re
import os
import pandas as pd

"""
统计总词数目
"""

stocks = [
    x for x in mongo_cli.get_database('wfp_com_page').collection_names(include_system_collections=False) if re.match(
        '^\d{6}$', x)]

data = []
i = 0
for stock in stocks:
    i += 1
    collection = mongo_cli.get_database('wfp_com_page').get_collection(stock)
    sum_words_num = 0
    for d in collection.find({}, projection={'words': 1}):
        sum_words_num += sum(d['words'].values())
    data.append([stock, sum_words_num])
    print('%d\t%s\t%d' % (i, stock, sum_words_num))

pd.DataFrame(data, columns=['stockCode', 'total_words_num']).to_csv('stock_total_words.csv', index=False)

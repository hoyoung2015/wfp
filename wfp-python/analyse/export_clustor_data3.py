from lib.wfp_com_page import get_stocks
import pandas as pd
from common.mongo import mongo_cli
import re
import math
import numpy as np

stocks = get_stocks()

big_clustor_map = {
    'x1': set(list(map(str, range(1, 6)))),
    'x2': set(list(map(str, range(6, 11)))),
    'x3': set(list(map(str, range(11, 13)))),
    'x4': set(list(map(str, range(13, 15)))),
    'x5': set(list(map(str, range(15, 20))))
}
print(big_clustor_map)


def get_key(key_in):
    for _k, _v in big_clustor_map.items():
        if key_in in _v:
            return _k


df = pd.DataFrame(index=stocks)
data_array = []
data_index = []
for d in mongo_cli.get_database('wfp').get_collection('word_vars').find():
    stock_code = d['stockCode']
    data_index.append(stock_code)
    word_vars = d['vars']
    data_map = {
        'stockCode': stock_code,
        'page_size': d['pageSize']
    }
    for k, v in word_vars.items():
        new_k = get_key(k)
        w_sum = sum(v.values())
        if new_k in data_map:
            data_map[new_k] += w_sum
        else:
            data_map[new_k] = w_sum
    data_array.append(data_map)
    # break

df2 = pd.DataFrame(data_array, index=data_index)

df = df.join(df2)

# 读取总词数
df3 = pd.read_csv('stock_total_words.csv', dtype={'stockCode': np.str})

df = df.join(df3.set_index('stockCode'))

print(df.head())

# print(df.head())
for x in df.columns:
    if re.match('x\d+', x):
        df[x] = df[x] / df['total_words_num']

print(df.head())
# 数据过滤，纠正偏分布
df = df.loc[df['x1'] < 0.003]
df = df.loc[df['x2'] < 0.01]
df = df.loc[df['x3'] < 0.01]
df = df.loc[df['x4'] < 0.0004]
df = df.loc[df['x5'] < 0.0015]

df.to_csv('clustor_data.csv', index=False)

print(df.shape)
# df.to_excel('clustor_data.xlsx')

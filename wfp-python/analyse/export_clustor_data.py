from lib.wfp_com_page import get_stocks
import pandas as pd
from common.mongo import mongo_cli
import re
import math
import numpy as np

stocks = get_stocks()

df = pd.DataFrame(index=stocks)
data_array = []
data_index = []
for d in mongo_cli.get_database('wfp').get_collection('word_vars').find():
    stock_code = d['stockCode']
    data_index.append(stock_code)
    word_vars = d['vars']
    data_map = {
        'page_size': d['pageSize']
    }
    for k, v in word_vars.items():
        data_map['x' + k] = sum(v.values())
    data_array.append(data_map)
    # break

df2 = pd.DataFrame(data_array, index=data_index)

df = df.join(df2)

# 读取总词数
df3 = pd.read_csv('stock_total_words.csv',dtype={'stockCode':np.str})

df = df.join(df3.set_index('stockCode'))

print(df.head())

# print(df.head())
for x in df.columns:
    if re.match('x\d+', x):
        df[x] = df[x] / df['page_size']

# print(df.head())
# 数据过滤，纠正偏分布
# df = df.loc[df['x1'] < 0.2]
# df['x1'] = df['x1'].apply(lambda x: math.sqrt(x + 1))

print(df.shape)
df.to_excel('clustor_data.xlsx')

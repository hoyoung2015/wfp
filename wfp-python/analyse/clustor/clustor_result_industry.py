from lib.company_info import get_industry, get_all_info, area_map, industry_map
import pandas as pd
import numpy as np

"""
统计聚类结果的地域分布

"""

com = get_all_info()

df = pd.read_csv('clustor_result_data.csv', dtype={'stockCode': np.str})
df = df.set_index('stockCode')
df = df.drop('page_size', axis=1)
df = df.drop('total_words_num', axis=1)

df = df.join(com)

df['industry'] = df['industry'].apply(get_industry)

cls_size = dict(df.groupby('industry').size())

print(df.groupby('industry').size())

data_array = []
for industry in industry_map.keys():
    tmp = df.loc[df['industry'] == industry]
    total = tmp.shape[0]
    size = tmp.groupby('result').size()
    d = dict(size)
    d['industry'] = industry
    d['industry_count'] = total
    data_array.append(d)

# area_size['result'] = 0
# data_array.append(area_size)
df = pd.DataFrame(data_array).fillna(0)

#
# def xfun(x):
#     if x.name in cls_size:
#         return x / cls_size[x.name]
#     return x
#
#
# df = df.apply(xfun)
print(df)
print('-' * 200)
for i in range(1, 6):
    df[i] = df[i] / df['industry_count']
print(df)

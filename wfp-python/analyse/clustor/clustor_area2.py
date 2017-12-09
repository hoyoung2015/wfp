from lib.company_info import get_area, get_all_info, area_map
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

df['area'] = df['area'].apply(lambda x: get_area(x))

cls_size = dict(df.groupby('result').size())

# print(cls_size)
# exit(0)

data_array = []
for area in area_map.keys():
    tmp = df.loc[df['area'] == area]
    total = tmp.shape[0]
    size = tmp.groupby('result').size()
    d = dict(size)
    d['area'] = area
    data_array.append(d)

# area_size['result'] = 0
# data_array.append(area_size)
df = pd.DataFrame(data_array).fillna(0)


def xfun(x):
    if x.name in cls_size:
        return x / cls_size[x.name]
    return x


df = df.apply(xfun)

print(df)

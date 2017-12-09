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

group_key = 'result'

cls_size = dict(df.groupby(group_key).size())

print(df.groupby(group_key).size())

# exit(0)

data_array = []
for i in range(1, 6):
    tmp = df.loc[df[group_key] == i]
    total = tmp.shape[0]
    size = tmp.groupby('industry').size()
    d = dict(size)
    d['result'] = i
    d['total'] = total
    data_array.append(d)

# area_size['result'] = 0
# data_array.append(area_size)
df = pd.DataFrame(data_array).fillna(0)


# tmp = df['total']

def xfun(x):
    if x.name not in ['total', 'result']:
        return x / df['total']
    else:
        return x


df = df.apply(xfun)
print(df)
print('-'*200)
print(df.T)
df.T.to_excel('clustor_result_industry.xlsx')

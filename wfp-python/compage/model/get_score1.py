from common.mongo import mongo_cli
import pandas as pd
import numpy as np
import math

word_vars_collection = mongo_cli.get_database('wfp').get_collection('word_vars')

all_data = []
all_index = []
for doc in word_vars_collection.find():
    vars_map = doc['vars']
    stock_code = doc['stockCode']
    page_size = doc['pageSize']
    green_size = doc['greenDocNum']
    row_data = []
    for k in range(1, 20):
        row_data.append(sum(vars_map[str(k)].values()))
    row_data.append(page_size)
    row_data.append(green_size)
    all_data.append(row_data)
    all_index.append(stock_code)
a = []
target = list(map(str, range(1, 20)))

df = pd.DataFrame(all_data, index=all_index, columns=(target + ['page_size', 'green_size']))

df.loc[df['2'] > 0, '2'] = 1

for k in target:
    if k == '2':
        continue
    df[k] = df[k] / df['page_size']

# 取指标数据

# target.remove('2')

# x6 = df['2']

df = df[target]

# 标准化
df = df.apply(lambda x: (x - np.min(x)) / (np.max(x) - np.min(x)))

df_y = pd.DataFrame({'score': df.sum(axis=1)})

# df_y = df_y.sort_values(by='score',ascending=False)
# print(df_y.head())
# exit(0)

# print(df_y.shape)
# print(df_y.head())
# df_y.applymap(lambda x: math.log(x + 1, math.e))
# print(df_y.head())
# exit(0)

# 读取因变量

index_list = []
all_xdata = []
for d in mongo_cli.get_database('wfp').get_collection('footprint').find({}, projection={'_id': 0}):
    """
    x1:股权分配，前十大股东持股比例
    x2:股权制衡，第二到第五大股东持股比例
    x3:资产负债比
    x4:公司规模，市值
    x5:网站权重
    x6:搜索引擎收录网页数
    x7:绿色专利数量
    x8:收益率


    c1:是否未环保业务
    """
    index_list.append(d['stockCode'])
    stock_percent = list(map(lambda x: float(x) / 100, d['ten_stock'].split(',')))
    if 'green_business' in d:
        green_business = d['green_business']
    else:
        green_business = 0
    try:
        row_data = {
            'x1': sum(stock_percent),
            'x2': sum(stock_percent[1:5]),
            'x3': d['fuzhaibi'],
            'x4': math.log(d['shareholders'] + 1, math.e),
            'x5': d['rank'],
            'x6': math.log(d['indexing'], math.e),
            # 'x7': math.log(d['green_patent_num'] + 1, math.e),
            'x7': d['green_patent_num'],
            'x8': d['shouyi'],
            'c1': green_business
        }
    except Exception as e:
        print(d['stockCode'])

    all_xdata.append(row_data)

df_x = pd.DataFrame(all_xdata, index=index_list)

# df_x['x6'] = x6

df = df_y.join(df_x)

df.loc[df['x7'] > 0, 'x7'] = 1

df.to_excel('web_green_data.xlsx', index=False, sheet_name='Sheet1')

from common.mongo import mongo_cli
import pandas as pd
import numpy as np

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

# df.loc[df['2'] > 0, '2'] = 1
# for k in target:
#     if k == '2':
#         continue
#     df[k] = df[k] / df['page_size']

# 取指标数据

# target.remove('2')

# x6 = df['2']

df = df[target]

# 标准化
df = df.apply(lambda x: (x - np.min(x)) / (np.max(x) - np.min(x)))

df_y = pd.DataFrame({'score': df.sum(axis=1)})

# 读取因变量

index_list = []
all_xdata = []
for d in mongo_cli.get_database('wfp').get_collection('footprint').find({}, projection={'_id': 0}):
    """
    x1:股权分配
    x2:资产负债比
    x3:公司规模，市值
    x4:网站权重
    x5:搜索引擎收录网页数
    """
    index_list.append(d['stockCode'])
    row_data = {
        'x1': sum(list(map(lambda x: (float(x) / 100) ** 2, d['ten_stock'].split(',')))),
        'x2': d['fuzhaibi'],
        'x3': d['shareholders'],
        'x4': d['rank'],
        'x5': d['indexing']
    }
    all_xdata.append(row_data)

df_x = pd.DataFrame(all_xdata, index=index_list)

# df_x['x6'] = x6

df = df_y.join(df_x)

df.to_excel('web_green_data.xlsx', index=False, sheet_name='Sheet1')

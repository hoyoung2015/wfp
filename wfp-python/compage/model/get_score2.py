from common.mongo import mongo_cli
import pandas as pd
import numpy as np
import math

bigram_collection = mongo_cli.get_database('wfp').get_collection('bigram')

all_data = []
all_index = []
for doc in bigram_collection.find():
    stock_code = doc['stockCode']
    all_data.append([doc['words_num'], doc['words_num2'], doc['words_num'], doc['total_page']])
    all_index.append(stock_code)

df = pd.DataFrame(all_data, index=all_index, columns=['words_num', 'words_num2', 'words_num3', 'total_page'])

df_y = pd.DataFrame({
    'score': df['words_num'],
    'score2': df['words_num2'],
    'score3': df['words_num3']
})
df_y = df_y.applymap(lambda x: math.log(x + 1, math.e))
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
            # 绿色专利名称写反了，patent_num是绿色专利数量，green_patent_num是总专利
            'x7': d['patent_num'],
            'x8': d['shouyi'],
            'c1': green_business
        }
    except Exception as e:
        print(d['stockCode'])
    all_xdata.append(row_data)

df_x = pd.DataFrame(all_xdata, index=index_list)

# df_x['x6'] = x6

df = df_y.join(df_x)

all_index.remove('002632')
all_index.remove('300066')
all_index.remove('000885')

# all_index.remove('600877')
# all_index.remove('600795')
df = df.loc[all_index]

print(df.sort_values(by='score', ascending=True).head())

# print(df.loc[df['score3'] > 12].sort_values(by='x1').head())

# df.loc[df['x7'] > 0, 'x7'] = 1

df.to_excel('web_green_data.xlsx', index=False, sheet_name='Sheet1')

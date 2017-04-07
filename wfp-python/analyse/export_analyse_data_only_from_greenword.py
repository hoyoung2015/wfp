from common.mongo import mongo_cli
import pandas as pd
import numpy as np
import math
from common.url_utils import get_domain
import re

"""
只通过匹配绿色关键词来计算得分
"""
df = pd.read_csv('green_words_count.csv', dtype={'stockCode': np.str})

df['score'] = df['greenWords'] / df['totalWords']

# print(df[['score', 'greenWords']].head())


# 读取财务信息
df_finance = pd.read_excel('2015数据处理.xlsx', converters={'bh': str, 'LISTAGE': int, 'STAGE()': int, 'PLU（）': int})
# df_finance['stockCode'] = df_finance['证券代码'].apply(lambda x: x[0:6])
# print(df_finance.columns)
df_finance = df_finance[['名称', 'bh', 'Size()', 'LEV()', 'ROA()', 'TOBIN\'Q()', 'FIN()',
                         'CAPIN()', 'PLU（）', 'BOARD()', 'INDR（）', 'FIRST', 'LISTAGE', 'STAGE()',
                         'VOLAT（W）', 'VOLAT（M）']]
df_finance.columns = ['NAME', 'stockCode', 'SIZE', 'LEV', 'ROA', 'TOBINQ', 'FIN',
                      'CAPIN', 'PLU', 'BOARD', 'INDR', 'FIRST', 'LISTAGE', 'STAGE',
                      'VOLAT_W', 'VOLAT_M']
df_finance = df_finance.set_index('stockCode')
df = df.set_index('stockCode')[['score']]
df = df.join(df_finance)
df = df[df['score'] < 0.05]
print(df.shape)
print(df.head())

df.to_excel('web_green_data_v2.xlsx')
exit(0)
"""
x1:股权分配，前十大股东持股比例
x2:股权制衡，第二到第五大股东持股比例
x3:资产负债比
x4:公司规模，市值，取了自然对数
x5:网站权重
x6:搜索引擎收录网页数，取了自然对数
x7:绿色专利数量，取了自然对数
x8:企业收益率
x9:新闻曝光数，取了自然对数

c1:是否为环保业务，控制变量，取0和1
"""

index_list = []
all_xdata = []
for d in mongo_cli.get_database('wfp').get_collection('footprint').find({}, projection={'_id': 0}):

    if d['stockCode'] not in stocks:
        continue

    index_list.append(d['stockCode'])
    stock_percent = list(map(lambda x: float(x) / 100, d['ten_stock'].split(',')))
    if 'green_business' in d:
        green_business = d['green_business']
    else:
        green_business = 0

    pagesize = 0
    if d['stockCode'] in stock_pagesize_map:
        pagesize = stock_pagesize_map[d['stockCode']]

    news_num = 0
    if 'news_num' in d:
        news_num = d['news_num']
    green_patent_percent = 0
    if d['patent_num'] > 0:
        green_patent_percent = d['green_patent_num'] / d['patent_num']
    try:
        row_data = {
            'assets': d['assets'],
            'rank': d['rank'],
            'indexing': d['indexing'],
            'indexing_www': d['indexing_www'],
            'outlink_az': d['outlink_num_az'],
            'outlink': d['outlink_num'],
            'green_patent_percent': green_patent_percent,
            'shouyi': d['shouyi'],
            'news_num': news_num,
            'patent_num': d['patent_num'],
            'green_patent_num': d['green_patent_num'],
            'c1': green_business
        }
    except Exception as e:
        print(d['stockCode'], web_source[d['stockCode']])
    all_xdata.append(row_data)

df_x = pd.DataFrame(all_xdata, index=index_list)

print(df.head())
exit(0)

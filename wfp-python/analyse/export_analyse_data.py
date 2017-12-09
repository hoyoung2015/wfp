from common.mongo import mongo_cli
import pandas as pd
import numpy as np
import math
from common.url_utils import get_domain
import re

stocks = [stock for stock in mongo_cli.get_database('wfp_com_page').collection_names(include_system_collections=False)
          if re.match('\d+', stock)]

web_source = {}
for d in mongo_cli.get_database('wfp').get_collection('web_source').find():
    web_source[d['stockCode']] = get_domain(d['webSite'])


# 只是用来读取总页面数量
bigram_collection = mongo_cli.get_database('wfp').get_collection('bigram_words')
all_data = []
all_index = []
for doc in bigram_collection.find():
    stock_code = doc['stockCode']
    all_data.append([doc['total'], doc['green_total']])
    all_index.append(stock_code)

df = pd.DataFrame(all_data, index=all_index, columns=['total', 'green_total'])

df_double = pd.read_excel('double_words.xlsx', sheetname='Sheet1', converters={'stockCode': str})

df = df.join(df_double.set_index('stockCode'))

df_total_words_num = pd.read_csv('stock_total_words.csv', dtype={'stockCode': np.str})

df = df.join(df_total_words_num.set_index('stockCode'))

# print(df.head())
# exit(0)
# k:stockCode,v:网页数
stock_pagesize_map = df['total'].to_dict()

df_y = pd.DataFrame({
    'EDI': df['double_words_num'] / df['total_words_num']
})

# df_y = df_y.applymap(lambda x: math.log(x + 1, math.e))
# 读取因变量

index_list = []
all_xdata = []
for d in mongo_cli.get_database('wfp').get_collection('footprint').find({}, projection={'_id': 0}):
    """
    FIRST:股权分配，前十大股东持股比例
    CP:股权制衡，第二到第五大股东持股比例
    LEV:资产负债比
    SIZE:公司规模，总资产
    RANK:网站权重
    INDEX:搜索引擎收录网页数
    PAT:是否拥有绿色专利，取0和1
    ROA:企业收益率
    NEWS:新闻聚合平台新闻曝光数

    CTRL:是否为环保业务（比如环保设备制造），控制变量，取0和1
    """

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
            'FIRST': sum(list(map(math.sqrt, stock_percent))),
            'CB': sum(list(map(math.sqrt, stock_percent[1:5]))),  # 开根号处理右偏
            'LEV': d['fuzhaibi'],
            'SIZE': d['assets'],
            'RANK': d['rank'],
            'indexing': d['indexing'],
            'INDEX': d['indexing_www'],
            'outlink_az': d['outlink_num_az'],
            'outlink': d['outlink_num'],
            'green_patent_percent': green_patent_percent,
            'ROA': d['shouyi'],
            'NEWS': news_num,
            'patent_num': d['patent_num'],
            'PAT': d['green_patent_num'],
            'CTRL': green_business
        }
    except Exception as e:
        print(d['stockCode'], web_source[d['stockCode']])
    all_xdata.append(row_data)

df_x = pd.DataFrame(all_xdata, index=index_list)

# df_x['x6'] = x6

df = df_y.join(df_x)

# print(df.sort_values(by='outlink', ascending=False).head())

# indexing_www
# all_index.remove('600352')
# all_index.remove('000570')

# x3资产负债比太大


# all_index.remove('002632')
# all_index.remove('000885')
#
# all_index.remove('002427')
# all_index.remove('002499')

# x4

# all_index.remove('300558')
# all_index.remove('300361')
# all_index.remove('300372')
# all_index.remove('601206')
# all_index.remove('600489')
# all_index.remove('000725')
# all_index.remove('600703')

# x8
# all_index.remove('000737')
# all_index.remove('000912')
# all_index.remove('000755')
# all_index.remove('000707')
# all_index.remove('600877')
# all_index.remove('603658')

# 剔除专利数为0的
# df = df.loc[df['x7'] > 0]
# df.loc[df['x7'] > 0, 'x7'] = 1
# df.loc[df['x7'] == 0, 'x7'] = 0


df = df.loc[all_index]
df = df.sort_values(by='EDI', ascending=False)

# 因为几乎有一半的企业是没有绿色专利的，所以这里只取0和1
df.loc[df['PAT'] == 0, 'PAT'] = 0
df.loc[df['PAT'] > 0, 'PAT'] = 1
# df['PAT'] = df['PAT'] / df['SIZE']

# df = df.loc[df['green_patent_percent'] < 0.8]
df['green_patent_percent'] = df['green_patent_percent'].apply(lambda x: math.sqrt(x))

# df = df.loc[df['RANK'] < 6]
#
df = df.loc[df['NEWS'] < 4500]
df = df.loc[df['NEWS'] > 20]
df['NEWS'] = df['NEWS'].apply(lambda x: math.sqrt(x))
#
#
df = df.loc[df['EDI'] < 1]
df['EDI'] = df['EDI'].apply(lambda x: math.sqrt(x))
#
df = df.loc[df['INDEX'] < 7000]
df = df.loc[df['INDEX'] > 20]
df['INDEX'] = df['INDEX'].apply(lambda x: math.log(x, math.e))
#
df = df.loc[df['SIZE'] < 900]
df['SIZE'] = df['SIZE'].apply(lambda x: math.log(x + 1, math.e))
#
# df = df.loc[df['ROA'] > -0.35]

df = df.loc[df['outlink_az'] < 70]



# 偏分处理
df['outlink_az'] = df['outlink_az'].apply(lambda x: math.log(x + 1, math.e))


# print(df.loc[df['green_patent_percent']==0].shape)

# df = df.loc[df['CTRL'] == 0]

print(df.shape)

df.to_excel('web_green_data.xlsx')

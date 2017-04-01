from common.mongo import mongo_cli
import pandas as pd
import numpy as np
import math

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

stock_pagesize_map = df['total'].to_dict()

df_y = pd.DataFrame({
    'score': df['double_words_num'] / df['total_words_num']
})

# df_y = df_y.applymap(lambda x: math.log(x + 1, math.e))
# 读取因变量

index_list = []
all_xdata = []
for d in mongo_cli.get_database('wfp').get_collection('footprint').find({}, projection={'_id': 0}):
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
            'x1': sum(stock_percent),
            'x2': sum(list(map(lambda x: math.sqrt(x), stock_percent[1:5]))),  # 开根号处理右偏
            'x3': d['fuzhaibi'],
            'x4': math.sqrt(d['assets']),
            'x5': d['rank'],
            'indexing': d['indexing'],
            'indexing_www': d['indexing_www'],
            'green_patent_percent': green_patent_percent,
            'x8': d['shouyi'],
            'x9': news_num,
            'patent_num': d['patent_num'],
            'green_patent_num': d['green_patent_num'],
            'c1': green_business
        }
    except Exception as e:
        print(d['stockCode'])
    all_xdata.append(row_data)

df_x = pd.DataFrame(all_xdata, index=index_list)

# df_x['x6'] = x6

df = df_y.join(df_x)

print(df.sort_values(by='indexing_www', ascending=False).head())

# indexing_www
all_index.remove('600352')
all_index.remove('000570')

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
df = df.sort_values(by='score', ascending=False)

df.to_excel('web_green_data.xlsx', sheet_name='Sheet1')

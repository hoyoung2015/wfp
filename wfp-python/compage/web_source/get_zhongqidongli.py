from common.mongo import mongo_cli
import pandas as pd
from common.pandas_utils import dict_local_type

'''
由中企动力这个企业开发的网站使用了，前端渲染，此代码列出检测出是中企动力开发的
'''
row = 0
df = pd.DataFrame(columns=['stockCode'])
for c in mongo_cli.get_database('tmp').get_collection('zhongqidongli').find({'target': 1}):
    df.loc[row] = [c['stockCode']]
    row += 1

# print(df)

df_source = pd.read_excel('/Users/baidu/Documents/web_source.xlsx', header=None, converters={0: str})
df_source.columns = ['stockCode', 'sname', 'webSite', 'sleepTime']

# print(df_source.head())
df = df.join(df_source.set_index('stockCode'), on='stockCode')
df['label'] = '中企动力'
row, col = df.shape
web_source = mongo_cli.get_database('wfp').get_collection('web_source')
for i in range(row):
    web_source.insert_one(dict_local_type(df.iloc[i].to_dict()))
mongo_cli.close()

# df.to_csv('web_source_zhongqidongli.txt', sep='\t',header=False,index=False)

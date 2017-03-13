from common.mongo import mongo_cli
import re
import pandas as pd
import numpy as np

db = mongo_cli.get_database('wfp_com_page')

collection_names = [c for c in db.collection_names(include_system_collections=False) if re.match('^\d{6}$', c)]

# [print(x) for x in collection_names]

df = pd.DataFrame(columns=['stockCode', 'pageCount'])

for i in range(len(collection_names)):
    collection_name = collection_names[i]
    df.loc[i] = [collection_name, db.get_collection(collection_name).count()]
    # break

df = df.sort_values(by='pageCount', ascending=False)

source_df = pd.read_csv('../web_source/web_source.txt', names=['stockCode', 'sname', 'webSite', 'sleepTime'],
                        dtype={'stockCode': np.str}, sep='\t')

df = df.join(source_df.set_index('stockCode'), on='stockCode')
df = df.drop('sleepTime', 1)
df['是否抓完'] = 0
df.loc[df['pageCount'] >= 1000, '是否抓完'] = 1
df['备注'] = ''

with pd.ExcelWriter('/Users/baidu/tmp/wfp_com_page.xlsx') as writer:
    df.to_excel(writer, index=False, sheet_name='Sheet1')
    pd.DataFrame({'是否抓取取值': [0, 1]}).to_excel(writer, index=False, sheet_name='meta')

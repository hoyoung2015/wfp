import pandas as pd
import numpy as np
import re
from common.mongo import mongo_cli

com = pd.read_csv('../../data/company_info.csv', dtype={'stockCode': np.str})
com = com[['stockCode', 'sname', 'name', 'webSite']]

stocks = []
for collection_name in mongo_cli.get_database('wfp_com_page').collection_names(include_system_collections=False):
    if re.match('\d{6}$', collection_name) is False:
        continue
    stocks.append(collection_name)

df = pd.DataFrame({'stockCode': stocks})

df = df.join(com.set_index('stockCode'), on='stockCode')
df['业务是否于环保相关'] = 0
df.to_excel('环保企业标注.xlsx', index=False, sheet_name='Sheet1')

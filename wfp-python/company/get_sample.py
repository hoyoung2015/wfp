from common.mongo import mongo_cli
import pandas as pd
import re
import numpy as np
from common.url_utils import get_domain

stocks = [
    x for x in mongo_cli.get_database('wfp_com_page').collection_names(include_system_collections=False) if re.match(
        '^\d{6}$', x)]

data_array = []
for d in mongo_cli.get_database('wfp').get_collection('company_info').find({'is_target': 1}):
    data_array.append([d['stockCode'], d['sname'], d['industry']])

df = pd.DataFrame(data_array, columns=['stockCode', 'sname', 'industry'])

df['股票代码'] = df['stockCode']

df = pd.DataFrame(index=stocks).join(df.set_index('stockCode'))

data_array = []
for d in mongo_cli.get_database('wfp').get_collection('web_source').find():
    data_array.append([d['stockCode'], d['webSite']])

df = df.join(pd.DataFrame(data_array, columns=['stockCode', 'webSite']).set_index('stockCode'))

random_indexes = np.random.randint(1, len(stocks), size=30)

df = df.ix[[stocks[i] for i in random_indexes]]
df['webSite'] = df['webSite'].apply(lambda x: 'http://' + get_domain(x))
df.columns = ['企业名称', '行业', '股票代码', '网址']
df.loc[:, ['企业名称', '股票代码', '行业', '网址']].to_excel('sample_company.xlsx', index=False)

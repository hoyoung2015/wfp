import pandas as pd
from lib.wfp_com_page import get_stocks
from common.mongo import mongo_cli
import numpy as np

df = pd.read_excel('web_green_data.xlsx', converters={'stockCode': str})

stocks = get_stocks()

data_array = []
for d in mongo_cli.get_database('wfp').get_collection('company_info').find({'is_target': 1}):
    data_array.append([d['stockCode'], d['sname'], d['industry']])

df2 = pd.DataFrame(data_array, columns=['stockCode', 'sname', 'industry'])

df = df.join(df2.set_index('stockCode'), on='stockCode')
df = df[['sname', 'stockCode', 'industry', 'score']]
df.to_excel('company_score_sample.xlsx')

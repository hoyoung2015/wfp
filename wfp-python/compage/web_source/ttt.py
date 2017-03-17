from common.mongo import mongo_cli
import pandas as pd
import numpy as np
from common.pandas_utils import dict_local_type

web_source = mongo_cli.get_database('wfp').get_collection('web_source')

web_source.create_index('stockCode', unique=True)

df = pd.read_csv('web_source.txt', sep='\t', names=['stockCode', 'sname', 'webSite', 'sleepTime'],
                 dtype={'stockCode': np.str})
# print(df.head())
row, col = df.shape



for i in range(row):
    web_source.insert_one(dict_local_type(df.iloc[i].to_dict()))

mongo_cli.close()

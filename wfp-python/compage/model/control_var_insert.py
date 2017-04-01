import pandas as pd
import numpy as np
import re
from common.mongo import mongo_cli

com = pd.read_excel('环保企业标注.xlsx', sheetname='Sheet1', converters={0: str})
collection = mongo_cli.get_database('wfp').get_collection('footprint')

for row in com.values:
    print(row[0], row[4])
    collection.update_one({'stockCode': row[0]}, {
        '$set': {
            'green_business': row[4]
        }
    })

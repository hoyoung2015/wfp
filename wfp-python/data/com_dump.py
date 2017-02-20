# -*- coding: utf-8 -*-

import pandas as pd
from pymongo import MongoClient

df = pd.read_csv('company_info.csv')
client = MongoClient(host='192.168.1.110',port=27017)
db = client.get_database('wfp')
collection = db.get_collection('com_info')
for index, row in df.iterrows():
    doc = {}
    for col_name in df.columns:
        doc[col_name] = row[col_name]
    collection.insert(doc)

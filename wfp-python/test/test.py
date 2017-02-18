__author__ = 'huyang'

from pymongo import MongoClient
from pymongo import ASCENDING
import re

client = MongoClient('192.168.1.110', 27017)

db = client.get_database('wfp_spider')
collection_names = db.collection_names()
sum = 0
for name in collection_names:
    if re.match('com_page_\d{6}', name):
        stock_code = name[9:15]
        # db.get_collection(name).create_index([('stockCode', ASCENDING), ('url', ASCENDING)], unique=True)
        cnt = db.get_collection(name).count({'stockCode': stock_code})
        print(stock_code, cnt)

        sum += cnt

print('total', sum)

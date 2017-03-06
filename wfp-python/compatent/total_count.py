from pymongo import MongoClient
import re

client = MongoClient(host='10.170.29.80', port=27017)
db = client.get_database('wfp_com_patent')

collection = db.get_collection('description')

sum = 0
for x in collection.find():
    sum += x['total']

print(sum)

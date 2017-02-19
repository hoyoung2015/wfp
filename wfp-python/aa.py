from pymongo import MongoClient

client = MongoClient(host='192.168.1.110', port=27017)
col = client.get_database('wfp_spider').get_collection('com_page_tmp')
for x in col.find({'stockCode': '002596'}):
    print(x)

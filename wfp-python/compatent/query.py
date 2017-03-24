from pymongo import MongoClient

client = MongoClient(host='10.170.65.118', port=27017)
db = client.get_database('wfp_com_patent')
collection = db.get_collection('600276')
for c in collection.find():
    print(c)

client.close()

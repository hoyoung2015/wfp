from pymongo import MongoClient
import re

host = '10.170.49.55'
port = 27017
client = MongoClient(host, port)

db = client.get_database('wfp_com_patent')

# 取前5个测试
test_collections = [c for c in db.collection_names(include_system_collections=False) if re.match('^\d{6}$', c)][:5]

db_test = client.get_database('wfp_com_patent_test')

for collection_name in test_collections:
    test_collection = db_test.get_collection(collection_name)
    test_collection.drop()
    count = db.get_collection(collection_name).count()
    n = 0
    for c in db.get_collection(collection_name).find(projection={'_id': 0}):
        n += 1
        test_collection.insert_one(c)
        print('\r%s\t %d/%d' % (collection_name, n, count), end='')
    print()

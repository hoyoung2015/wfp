from pymongo import MongoClient

client = MongoClient(host='192.168.1.110', port=27017)
db = client.get_database('wfp_com_page')
for name in db.collection_names(include_system_collections=False):
    if name.endswith('_tmp'):
        continue
    cnt = db.get_collection(name).count()
    print(name, cnt)
    if cnt == 0:
        db.get_collection(name).drop()

from pymongo import MongoClient
import re

client = MongoClient(host='10.170.47.245', port=27017)
db = client.get_database('wfp_com_patent')
cnt = 0
for name in db.collection_names(include_system_collections=False):
    if re.match('\d{6}_tmp', name):
        cnt += 1
        print('%d\t%s\t%d' % (cnt, name, db.get_collection(name).count()))

from pymongo import MongoClient
import re

client = MongoClient(host='127.0.0.1', port=27017)

db = client.get_database('wfp_com_page')
for name in db.collection_names():
    if re.match('\d{6}', name):
        total = db.get_collection(name).count()
        print('%s\t%d' % (name, total))

client.close()
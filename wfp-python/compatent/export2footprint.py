from common.mongo import mongo_cli
import re

com_money = {}
db = mongo_cli.get_database('wfp_com_patent')
patent_map = {}
for collection_name in db.collection_names(include_system_collections=False):
    if re.match('^\d{6}$', collection_name) is False:
        continue
    collection = db.get_collection(collection_name)
    total = collection.count()
    total_green = collection.count({'green_num': {'$gt': 0}})
    patent_map[collection_name] = (total, total_green)
print(patent_map)
collection = mongo_cli.get_database('wfp').get_collection('footprint')
for x in collection.find():
    patent_num, green_patent_num = patent_map[x['stockCode']]
    collection.update_one({'_id': x['_id']}, {'$set': {'patent_num': patent_num, 'green_patent_num': green_patent_num}})

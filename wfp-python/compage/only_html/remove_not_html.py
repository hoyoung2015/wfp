from common.mongo import mongo_cli
import re

"""
删除所有非html页面
"""
db = mongo_cli.get_database('wfp_com_page')

collection_names = [c for c in db.collection_names(include_system_collections=False) if re.match('^\d{6}$', c)]

web_source_collection = mongo_cli.get_database('wfp').get_collection('web_source')

web_source_map = {}
for doc in web_source_collection.find():
    web_source_map[doc['stockCode']] = doc['webSite']

filters = {
    'html': {
        '$exists': False
    }
}

for collection_name in collection_names:
    collection = db.get_collection(collection_name)
    total = collection.count(filters)
    if total > 0:
        collection.delete_many(filters)
    print('%s\t%d\t%s' % (collection_name, total, web_source_map[collection_name]))

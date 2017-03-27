from common.mongo import mongo_cli
import hashlib
import re

db = mongo_cli.get_database('wfp_com_page')

collection_names = [c for c in db.collection_names(include_system_collections=False) if re.match('^\d{6}$', c)]

num = 0
for collection_name in collection_names:
    num += 1
    collection = db.get_collection(collection_name)
    filters = {
        'content': {
            '$exists': True
        }
    }
    total = collection.count(filters)
    cnt = 0
    for doc in collection.find(filters, projection={'content': 1}):
        cnt += 1
        content = doc['content']
        sha1 = hashlib.sha1()
        sha1.update(content.encode('utf-8'))
        collection.update_one({'_id': doc['_id']}, {'$set': {'contentSha1': sha1.hexdigest()}})
        print('\r%d-%s\t%d/%d' % (num, collection_name, cnt, total), end='')
        # break
    print()
    # break

from common.mongo import mongo_cli
import re
import pymongo

if __name__ == '__main__':
    db = mongo_cli.get_database('wfp_com_page')
    sha1_distinct_collection = mongo_cli.get_database('tmp').get_collection('sha1_distinct')
    sha1_distinct_collection.create_index([('stockCode', pymongo.ASCENDING)], unique=True)
    collection_names = [c for c in db.collection_names(include_system_collections=False) if re.match('^\d{6}$', c)]
    cnt = 0
    for collection_name in collection_names:
        cnt += 1
        if sha1_distinct_collection.count({'stockCode': collection_name}) > 0:
            print('\r%d-%s\tfinished' % (cnt, collection_name), end='')
            print()
            continue
        collection = db.get_collection(collection_name)
        total = collection.count()
        filters = {
            'contentSha1': {
                '$exists': True
            }
        }
        total_html = collection.count(filters)
        del_list = []
        sha1_set = set()
        cnt2 = 0
        for doc in collection.find(filters, projection={'contentSha1': 1}):
            cnt2 += 1
            contentSha1 = doc['contentSha1']
            print('\r%d-%s\t%d/%d\t%d' % (cnt, collection_name, cnt2, total_html, len(del_list)), end='')
            if contentSha1 in sha1_set:
                del_list.append(doc['_id'])
            else:
                sha1_set.add(contentSha1)
        if len(del_list) > 0:
            collection.delete_many({
                '_id': {
                    '$in': del_list
                }
            })
        sha1_distinct_collection.insert_one(
            {'stockCode': collection_name, 'total': total, 'total_html': total_html, 'total_del': len(del_list)})
        print()
        # break
    mongo_cli.close()

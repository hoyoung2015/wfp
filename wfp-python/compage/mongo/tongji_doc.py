from common.mongo import mongo_cli
import re

db = mongo_cli.get_database('wfp_com_page')
collection_names = [c for c in db.collection_names(include_system_collections=False) if re.match('^\d{6}$', c)]

num = 0
for collection_name in collection_names:
    collection = db.get_collection(collection_name)
    filters = {
        '$or': [{'url': {
            '$regex': '\.(pdf|PDF|doc|DOC|docx|DOCX)$'
        }}, {'contentType': {
            '$in': ['pdf', 'msword']
        }}]
    }
    total_doc = collection.count()
    num += total_doc
    print('%s\t%d' % (collection_name, total_doc))

print(num)

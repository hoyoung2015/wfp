import sys
import os

curPath = os.path.abspath(os.path.dirname(__file__))
rootPath = os.path.split(curPath)[0]
sys.path.append(rootPath)


from common.mongo import mongo_cli
import re
import json


def load_green_patents(file='patents.json'):
    _green_patents_set = set()
    with open(file) as fp:
        for k, v in json.load(fp).items():
            if '/' not in k:
                continue
            _green_patents_set.add(k)
    return _green_patents_set


if __name__ == "__main__":
    db_name = sys.argv[1]
    green_patents_set = load_green_patents('patents.json')
    db = mongo_cli.get_database(db_name)
    collection_names = [name for name in db.collection_names(include_system_collections=False) if
                        re.match('^\d+$', name)]
    total_collection = len(collection_names)
    cnt_collection = 0
    for collection_name in collection_names:
        cnt_collection += 1
        collection = db.get_collection(collection_name)
        total_patent = collection.count()
        cnt_patent = 0
        for patent in collection.find(projection={'classId': 1}):
            cnt_patent += 1
            print('\r%d-%s\t%d/%d' % (cnt_collection, collection_name, cnt_patent, total_patent), end='')
            if 'classId' not in patent or patent['classId'] == '':
                continue
            patents_set = set()
            green_num = 0
            for pid in patent['classId'].split(','):
                if '/' not in pid or pid in patents_set:
                    continue
                patents_set.add(pid)
                if pid in green_patents_set:
                    green_num += 1
            collection.update({'_id': patent['_id']}, {'$set': {'green_num': green_num}})
        print()
    mongo_cli.close()

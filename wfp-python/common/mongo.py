__author__ = 'huyang'
from common import config
from pymongo import MongoClient


mongo_cli = MongoClient(host=config.get('mongodb','host'),port=config.getint('mongodb','port'))


def copy_collection(src={'db': None, 'host': None, 'port': 27017}, dist={'db': None, 'host': None, 'port': 27017},
                    collection={}):
    src_db = MongoClient(host=src['host'], port=src['port']).get_database(src['db'])
    dist_db = MongoClient(host=dist['host'], port=dist['port']).get_database(dist['db'])
    for src_col_name, dist_col_name in collection.items():
        src_col = src_db.get_collection(src_col_name)
        dist_col = dist_db.get_collection(dist_col_name)
        count = src_col.count()
        num = 0
        for doc in src_col.find(projection={'_id': 0}):
            dist_col.insert_one(doc)
            num += 1
            print('\r[%s]%s.%s->[%s]%s.%s\t%d/%d' % (
            src['host'], src['db'], src_col_name, dist['host'], dist['db'], dist_col_name, num, count), end='')
        print()
    print('task complete')


if __name__=='__main__':
    pass
    # wfp = mongo_cli.get_database('wfp')
    # company_info = wfp.get_collection('company_info')
    # for x in company_info.find():
    #     print(x)
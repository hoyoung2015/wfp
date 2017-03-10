from pymongo import MongoClient

remote = MongoClient('cp01-rdqa04-dev148.cp01', 8017)
local = MongoClient('127.0.0.1', 27017)

dbname = 'wfp'
colname = 'company_info'

remote_db = remote.get_database(dbname)
local_db = local.get_database(dbname)

remote_col = remote_db.get_collection(colname)
local_col = local_db.get_collection(colname)

for c in remote_col.find({}, projection={'_id': 0}):
    local_col.insert_one(c)
    print(c['stockCode'])
remote.close()
local.close()

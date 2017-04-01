from common.mongo import mongo_cli

com_money = {}
for x in mongo_cli.get_database('wfp').get_collection('company_info').find():
    if 'shareholders' in x:
        com_money[x['stockCode']] = x['shareholders']
    else:
        com_money[x['stockCode']] = 0

collection = mongo_cli.get_database('wfp').get_collection('footprint')
for x in collection.find():
    collection.update_one({'_id': x['_id']}, {'$set': {'shareholders': com_money[x['stockCode']]}})

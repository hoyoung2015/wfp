from common.mongo import mongo_cli

collection = mongo_cli.get_database('wfp_com_page').get_collection('000055')

filter = {
    'url': {
        '$regex': 'http://www.fangda.com/uploadfiles.+'
    }
}
for row in collection.find(filter):
    print(row)
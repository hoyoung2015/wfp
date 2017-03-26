from common.mongo import mongo_cli

rs = mongo_cli.get_database('wfp_com_page').drop_collection('603663')
# rs = mongo_cli.get_database('wfp_com_page').get_collection('603663').count()
print(rs)
mongo_cli.close()
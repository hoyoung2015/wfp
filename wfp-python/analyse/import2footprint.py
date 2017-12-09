from common.mongo import mongo_cli

footprint = mongo_cli.get_database('wfp').get_collection('footprint')



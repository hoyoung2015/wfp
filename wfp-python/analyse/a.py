from common.mongo import mongo_cli

mongo_cli.get_database('wfp').get_collection('footprint').update_many({},{
    '$set':{
        'assets':0
    }
})
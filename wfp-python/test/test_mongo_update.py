from common.mongo import mongo_cli

if __name__=="__main__":
    conn = mongo_cli.get_database("wfp").get_collection("company_info")
    conn.update_one({
        "stockCode":'600018'
    },{
        "$set":{
            "is_access": 0
        }
    })
# -*- coding: utf-8 -*-
__author__ = 'v_huyang01'
from pymongo import MongoClient
if __name__=="__main__":
    host = "cp01-rdqa04-dev148.cp01"
    port = 8017
    db_wfp = "wfp"
    mongo = MongoClient("cp01-rdqa04-dev148.cp01", 8017)
    conn = mongo.get_database("wfp").get_collection("company_info")
    stock_codes = [x['stockCode'] for x in conn.find({"is_target":1},{"_id":0,"stockCode":1})]
    print(len(stock_codes))
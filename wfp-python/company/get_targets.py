# -*- coding: utf-8 -*-
__author__ = 'v_huyang01'
from common.mongo import mongo_cli
if __name__=="__main__":
    conn = mongo_cli.get_database("wfp").get_collection("company_info")
    stock_codes = [x['stockCode'] for x in conn.find({"is_target":1},{"_id":0,"stockCode":1})]
    print(len(stock_codes))
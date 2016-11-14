# -*- coding: utf-8 -*-
__author__ = 'v_huyang01'
import pymongo
import os
import sys
import time
import logging
import logging.config
from zrbg import downloader
if __name__ == "__main__":

    logging.config.fileConfig("../logging.conf")
    logger = logging.getLogger('zrbg_downloader')

    mongo = pymongo.MongoClient("cp01-rdqa04-dev148.cp01", 8017)
    conn = mongo.get_database("wfp").get_collection("zrbg_info")
    conn_company = mongo.get_database("wfp").get_collection("company_info")

    # 查询目标行业的股票代码
    stock_codes = [x['stockCode'] for x in conn_company.find({"is_target":1},{"_id":0,"stockCode":1})]
    # print(len([x['stockCode'] for x in conn.find({"Hstock": {"$ne": ""},"stockCode":{"$in":stock_codes}},{"stockCode":1,"industry":1,"year":1,"Hstock":1,"_id":0}) if x['year']=='2015-12-31']))
    # sys.exit(1)

    count = 0
    for x in conn.find({"Hstock": {"$ne": ""},"stockCode":{"$in":stock_codes}},{"stockCode":1,"industry":1,"year":1,"Hstock":1,"_id":0}):
        print(x)
        if not os.path.exists("pdf"):
            os.mkdir("pdf")
        output = "pdf"+"/"+x['year'][:4]+"_"+x['industry'].replace("*","").replace("(","_")[:-1]+".pdf"
        if os.path.exists(output):
            print("已存在")
            continue
        url = x['Hstock']
        try:
            downloader.download(url, output, blocks=2, proxies={})
            count += 1
            print("\n完成:"+str(count))
            # time.sleep(2)
        except:
            logger.error("出错:"+url)
        # break

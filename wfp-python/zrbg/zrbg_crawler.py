# -*- coding: utf-8 -*-
"""
获取责任报告的下载地址
http://stockdata.stock.hexun.com/zrbg/
"""
__author__ = 'hoyoung'
from pymongo import MongoClient
from urllib import request
import time
import demjson
import re
import random
import logging

try:
    import xml.etree.cElementTree as ET
except ImportError:
    import xml.etree.ElementTree as ET


class ZrbgCrawler:
    host = "cp01-rdqa04-dev148.cp01"
    port = 8017
    db_wfp = "wfp"
    years = [
        # "2010-12-31",
        # "2011-12-31",
        # "2012-12-31",
        # "2013-12-31",
        # "2014-12-31",
        "2015-12-31"
    ]

    def __init__(self):
        self.mongoClient = MongoClient(self.host, self.port)
        self.db = self.mongoClient.get_database(self.db_wfp)

    def parse(self, year, jsonObject):
        conn = self.db.get_collection("zrbg_info")

        for obj in jsonObject['list']:
            # print(obj)
            obj["stockCode"] = re.findall("\d+", obj["industry"])[0]
            obj["year"] = year
            try:
                obj["Hstock"] = ET.fromstring(obj["Hstock"]).attrib["href"]
            except ET.ParseError:
                obj["Hstock"] = ""
            try:
                obj["Wstock"] = ET.fromstring(obj["Wstock"]).attrib["href"]
            except ET.ParseError:
                obj["Wstock"] = ""

            # 删除没用的
            obj.pop("Tstock")
            if conn.find_one({
                "stockCode": obj["stockCode"],
                "year": year
            }) is None:
                print("插入" + obj["stockCode"] + "-" + year + ":" + obj["Hstock"])
                conn.insert(obj)
            else:
                print("已存在" + obj["stockCode"] + "-" + year)

    def run(self):
        for year in self.years:
            page = 1
            hasNextPage = True
            retry = 0
            year_total = 0
            while hasNextPage:
                count = 20
                round_time = round(time.time() * 1000)
                url = "http://stockdata.stock.hexun.com/zrbg/data/zrbList.aspx?date=%s&count=%d&pname=20&titType=null&page=%d&callback=hxbase_json1%s" % (
                    year, count, page, str(round_time))
                req = request.Request(url)
                req.add_header("Host", "stockdata.stock.hexun.com")
                req.add_header("User-Agent",
                               "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.71 Safari/537.36")
                print(url)
                resp = request.urlopen(req)
                if resp.code == 200:

                    try:
                        json_str = resp.read().decode("gb2312")[13:-1]
                    except UnicodeDecodeError:
                        print("gb2312解码出错:"+url)
                        page += 1
                        continue
                    data = demjson.decode(json_str)
                    if len(data['list']) == 0:
                        hasNextPage = False
                    else:
                        # 解析
                        self.parse(year, data)
                        year_total += count
                        print(year+"->"+str(year_total))
                        page += 1
                    retry = 0
                else:
                    if retry < 3:
                        retry += 1
                    else:
                        retry = 0
                # 休眠1到4秒
                time.sleep(random.randint(1, 4))


if __name__ == "__main__":
    zrbg = ZrbgCrawler()
    zrbg.run()


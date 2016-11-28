# -*- coding: utf-8 -*-
"""
标记污染行业
"""
from common.mongo import mongo_cli


if __name__=="__main__":
    wfp = mongo_cli.get_database('wfp')
    company_info = wfp.get_collection("company_info")
    for com in open('','r').readlines():
        print(com)

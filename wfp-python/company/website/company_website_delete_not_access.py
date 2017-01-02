# -*- coding: utf-8 -*-
"""
删除不可访问的企业
"""
__author__ = 'huyang'
from common.mongo import mongo_cli

if __name__ == "__main__":
    wfp = mongo_cli.get_database('wfp')
    company_info = wfp.get_collection('company_info')

    for line in open("not_access_list.txt").readlines():
        rs = company_info.update_one({
            "stockCode":line[:-1]
        },{
            "$set":{
                "is_target":0
            }
        })
        print(rs)

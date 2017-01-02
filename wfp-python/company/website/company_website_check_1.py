# -*- coding: utf-8 -*-
"""
校验步骤：
1.判断webSite字段是否存在，不存在直接删除
2.判断webSite格式是否正确，不正确加入格式错误列表
"""
__author__ = 'huyang'
from common.mongo import mongo_cli
import re

if __name__ == "__main__":
    wfp = mongo_cli.get_database('wfp')
    company_info = wfp.get_collection('company_info')
    web_site_not_exists = list()
    web_site_format_error = list()
    for com in company_info.find({'is_target': 1}, {'_id': 0}):
        web_site = com['webSite']
        if web_site == None:
            # 直接删除
            rs = company_info.delete_one({'stockCode':com['stockCode']})
            print(rs)
        elif re.match('^https?:/{2}\w.+$', web_site) == None or ';' in web_site:
            web_site_format_error.append('%s,%s,%s' % (com['stockCode'], com['name'], web_site))
        else:
            print('%s,%s,%s' % (com['stockCode'], com['name'], web_site))

    print("站点格式有误:")
    for s in web_site_format_error:
        print(s)

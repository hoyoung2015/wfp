# -*- coding: utf-8 -*-
"""
测试代理
"""
import requests
from baidu_crawler import user_agents

# ip_proxy = 'http://hoyoung:QWerASdf@123.206.58.101:8128'
# ip_proxy = 'http://202.121.179.59:8080'
ip_proxy = 'http://222.211.65.138:80'
# ip_proxy = 'http://103.8.193.21:8080'

headers = {'User-Agent': user_agents.get_random_pc_agent()}
try:

    param = {
        "requestModule": "PatentSearch",
        "userId": "",
        "patentSearchConfig": {
            "Query": "AS=日出东方太阳能股份有限公司",
            "TreeQuery": "",
            "Database": "wgzl,syxx,fmzl",
            "Action": "Search",
            "DBOnly": 0,
            "Page": "0",
            "PageSize": "20",
            "GUID": "",
            "Sortby": "-IDX,PNM",
            "AddOnes": "",
            "DelOnes": "",
            "RemoveOnes": "",
            "SmartSearch": "",
            "TrsField": ""
        }
    }
    resp = requests.post('http://www.innojoy.com/client/interface.aspx', data=None, json=param, proxies={
        'http': ip_proxy
    }, timeout=40)

    # resp = requests.get('http://1212.ip138.com/ic.asp', headers=headers, proxies={
    #     'http': ip_proxy
    # }, timeout=40)
    print(resp.text)
except Exception as e:
    print(e)

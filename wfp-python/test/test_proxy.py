# -*- coding: utf-8 -*-
"""
测试代理
"""
import requests
from baidu_crawler import user_agents

headers = {'User-Agent': user_agents.get_random_pc_agent()}

c = requests.cookies.RequestsCookieJar()
s = requests.Session()
try:

    r = requests.get('http://www.valin.cn', headers=headers, timeout=40)
    s.cookies.update(r.cookies)
    print(s.cookies)
    r = requests.get('http://www.valin.cn/?security_verify_data=313336362c373638', headers=headers, timeout=40)
    print(r.text)
except Exception as e:
    print(e)

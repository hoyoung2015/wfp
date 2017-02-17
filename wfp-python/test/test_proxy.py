# -*- coding: utf-8 -*-
"""
测试代理
"""
import requests
from baidu_crawler import user_agents

ip_proxy = 'http://hoyoung:QWerASdf@123.206.58.101:8128'
# ip_proxy = 'http://hoyoung:QWerASdf@139.129.93.2:8128'
# ip_proxy = 'http://hoyoung:QWerASdf@182.61.20.189:8128'
# ip_proxy = 'http://hoyoung:QWerASdf@138.128.203.167:8128'

headers = {'User-Agent': user_agents.get_random_pc_agent()}
try:

    resp = requests.get('http://1212.ip138.com/ic.asp', headers=headers, proxies={
        'http': ip_proxy
    }, timeout=40)
    print(resp.text)
except Exception as e:
    print(e)

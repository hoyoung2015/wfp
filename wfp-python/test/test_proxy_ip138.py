# -*- coding: utf-8 -*-
"""
测试代理
"""
import requests
from baidu_crawler import user_agents

ip_proxy = '85.15.69.131:8081'

headers = {
    'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
    'Accept-Encoding': 'gzip, deflate, sdch',
    'Accept-Language': 'zh-CN,zh;q=0.8,en;q=0.6',
    'User-Agent': user_agents.get_random_pc_agent()}
try:
    resp = requests.get('http://1212.ip138.com/ic.asp', headers=headers, proxies={
        'http': ip_proxy
    }, timeout=40)
    print(resp.content.decode('gb2312'))
except Exception as e:
    print(e)

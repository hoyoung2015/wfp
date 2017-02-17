# -*- coding: utf-8 -*-
"""
测试代理
"""
import requests
from baidu_crawler.user_agents import pc_agents
import time

ip_proxy = '139.224.232.56:80'

try:
    cnt = 4
    for a in pc_agents:
        headers = {
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'zh-CN,zh;q=0.8',
            'Cache-Control': 'max-age=0',
            'User-Agent': a
        }
        print(a)
        resp = requests.get('https://www.baidu.com', headers=headers, proxies={
            'http': ip_proxy
        }, timeout=40)
        with open('page' + str(cnt) + '.html', 'w+') as f:
            f.write(resp.text)
            f.close()
        print(resp.text)
        print("-" * 100)
        cnt += 1
        time.sleep(3)
except Exception as e:
    print(e)

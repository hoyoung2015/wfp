# -*- coding: utf-8 -*-
"""
测试代理
"""
import requests
from baidu_crawler import user_agents
from baidu_crawler.user_agents import agents
import time
import random

ip_proxy = '139.224.232.56:80'

try:
    cnt = 4
    for a in agents:
        headers = {
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'zh-CN,zh;q=0.8',
            'Cache-Control': 'max-age=0',
            'User-Agent': user_agents.get_random_pc_agent()
        }
        print(cnt, a)
        resp = requests.get(
            'https://www.baidu.com/link?url=BmXx8ebq9kmPg2LMFbHtPBXqVKmmc2xC0koW9KETxdZgt3lrdEb5EgFCKF6ABDVlvh9GvTB9BuvfGt26Kh6Vzq&wd=&eqid=bf18d8c400042d170000000358774875',
            headers=headers, proxies={
                'http': ip_proxy
            }, timeout=40)
        with open('page' + str(cnt) + '.html', 'w+') as f:
            f.write(resp.text)
            f.close()
        print(resp.text)
        print("-" * 100)
        cnt += 1
        time.sleep(random.randint(2, 8))
except Exception as e:
    print(e)

# -*- coding: utf-8 -*-
"""
测试代理
"""
import requests
from baidu_crawler import user_agents

headers = {'User-Agent': user_agents.get_random_pc_agent()}
try:

    resp = requests.get('http://www.valin.cn/', headers=headers, timeout=40)

    print(resp.text)
except Exception as e:
    print(e)

# -*- coding: utf-8 -*-
"""
测试代理
"""
import requests
from baidu_crawler import user_agents

ip_proxy = '139.224.232.56:80'

try:
    headers = {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'Accept-Language': 'zh-CN,zh;q=0.8',
        'Cache-Control': 'max-age=0',
        'User-Agent': user_agents.get_random_pc_agent()
    }
    params = {
        "ie": "utf-8",
        "mod": 1,
        "isbd": 1,
        # "isid": "bf18d8c400042d17",
        "wd": "site:wisco.com.cn",
        "pn": 750,
        "oq": "site:wisco.com.cn",
        "usm": 1,
        "rsv_idx": 1,
        # "rsv_pq": "bf18d8c400042d17",
        # "rsv_t": "ad86YLDxLsWqTldAF+OkurtAu6blzd1jlZQJZ62n1D/wO/edsE9ZN1mIN5g",
        "bs": "site:wisco.com.cn",
        # "rsv_sid": "1439_13701_21115_21943_17001_20930",
        "_ss": 1,
        # "clist": "a8d487685bc5272",
        "f4s": 1,
        "csor": 17,
        "_cr1": 26777
    }
    resp = requests.get(
        'https://www.baidu.com/s?', params=params,
        headers=headers, proxies={
            'http': ip_proxy
        }, timeout=40)
    print(resp.text)
    with open('detail.html', 'w+') as f:
        f.write(resp.text)
        f.close()
except Exception as e:
    print(e)

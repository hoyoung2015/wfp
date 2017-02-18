import requests
from common import user_agents


def test_proxy(proxy=''):
    headers = {'User-Agent': user_agents.get_random_pc_agent()}
    try:
        resp = requests.get('http://1212.ip138.com/ic.asp', headers=headers, proxies={
            'http': proxy
        }, timeout=10)
        print(resp.text)
    except Exception as e:
        print('proxy %s is error' % proxy)


with open('../../wfp-spider/src/main/resources/proxy.txt') as f:
    for line in f.readlines():
        s = line.strip().replace('\n', '')
        if s.startswith('#'):
            continue
        arr = s.split(',')
        proxy = 'http://%s:%s@%s:%s' % (arr[0], arr[1], arr[2], arr[3])
        print(proxy)
        test_proxy(proxy)

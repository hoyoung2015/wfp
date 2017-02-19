import requests
from common import user_agents
url = 'http://dl.sungrowpower.com/index.php?s=/Home/File/download_file/id/520/name/SG5KTL-D%E4%BA%A7%E5%93%81%E4%BB%8B%E7%BB%8D.html'
# url = 'http://www.hoyoung.net/'
headers = {
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'zh-CN,zh;q=0.8',
            'Cache-Control': 'max-age=0',
            'User-Agent': user_agents.get_random_pc_agent()
        }
try:
    resp = requests.get(url, headers=headers)
    print(resp.headers['Content-Description'])
    print(resp.headers['Content-Disposition'])
except Exception as e:
    print(e)

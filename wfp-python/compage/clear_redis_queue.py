import sys
import os

curPath = os.path.abspath(os.path.dirname(__file__))
rootPath = os.path.split(curPath)[0]
print(rootPath)
sys.path.append(rootPath)


from redis import Redis
import re
from common import url_utils

'''
处理抓取过程中发现的大量的不需要的页面
比如企业自建的discuz论坛，商城页面，这些页面量非常大，但是不需要
'''

rds = Redis(host='127.0.0.1', port=6379, db=0, decode_responses=True)

url = 'http://www.unilumin.cn/'
regex = 'http://(ja|eps).unilumin.com/.*|http://www.unilumin.cn/sitefiles/services/cms/.*|http://www.unilumin.cn/channel/(video|gaoqingdianshiqiang|HDTV|luoyan3D|huneigudinganzhuang|户内外租赁|changjing|ptsb|OutDoorLight|_55[678]).*'
domain = url_utils.get_domain(url)
# exit(-1)

key = 'queue_' + domain

total = rds.llen(key)
while total > 0:
    total -= 1
    url = rds.lpop(key)
    if url is None:
        break
    # print(total,'\t',url)
    if total % 100 == 0:
        print(total)
    if re.match(regex, url):
        pass
    else:
        rds.rpush(key, url)

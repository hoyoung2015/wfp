from redis import Redis
import re
from common import url_utils

'''
处理抓取过程中发现的大量的不需要的页面
比如企业自建的discuz论坛，商城页面，这些页面量非常大，但是不需要
'''

rds = Redis(host='10.170.61.54', port=6379, db=0, decode_responses=True)

# url = 'http://www.yuhong.com.cn/'
# regex = 'http://www.yuhong.com.cn/(technology.*|noticeshow.*|purchase.*|project.*|logistics.*|strategy.*|service.*|download.*|newsshow.*|affirming.*|contactform.*|danye.*|videoshow.*|product.*){2,}'

url = 'http://www.huaxincem.com'
regex = 'http://biz.huaxincem.com/.*'

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

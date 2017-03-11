from redis import Redis
import re
from common import url_utils

'''

'''

rds = Redis(host='10.170.4.57', port=6379, db=0, decode_responses=True)

url = 'http://www.sanju.cn'
domain = url_utils.get_domain(url)
# exit(-1)

key_queue = 'queue_' + domain
key_set = 'set_' + domain
key_item = 'item_' + domain

rs = rds.delete(key_queue, key_set, key_item)

print(rs)

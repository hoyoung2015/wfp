# from common.mongo import mongo_cli
from common import url_utils
import re

stock_code = '600970'

domain = None
# 读域名
with open('../../wfp-spider/src/main/shell/bin/site03.txt') as f:
    for line in f.readlines():
        line = line.strip().replace('\n', '')
        if line == '' or line.startswith('#'):
            continue
        if line.startswith(stock_code):
            domain = url_utils.get_domain(line.split('\t')[2]).replace('www.', '')
            break
    f.close()
if domain is None:
    print('stock_code not exists')
    exit(0)

print(domain)

# 读规则
with open('../../wfp-spider/src/main/resources/domain_url_black_list.txt') as f:
    for line in f.readlines():
        line = line.strip().replace('\n', '')
        if line == '' or line.startswith('#'):
            continue
        if re.match('\[%s\]' % domain, line):
            print(line)
            break
    f.close()

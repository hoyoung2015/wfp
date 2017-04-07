from common.mongo import mongo_cli
from common.url_utils import get_domain
import re

"""
统计处域名是ip的网站
"""

for d in mongo_cli.get_database('wfp').get_collection('web_source').find():
    web_site = d['webSite']
    domain = get_domain(web_site)
    if re.match('(\d+\.){3}\d+(:\d+)?', domain):
        print(d['stockCode'], domain)

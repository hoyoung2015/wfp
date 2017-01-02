__author__ = 'huyang'
from common.mongo import mongo_cli

mongo_cli.get_database("wfp")
wfp = mongo_cli.get_database('wfp')
company_info = wfp.get_collection('company_info')
for x in company_info.find({"is_target":1}):
    print(x['stockCode']+"\t"+x['sname'] + "\t" +x['webSite'])
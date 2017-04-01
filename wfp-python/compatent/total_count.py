from common.mongo import mongo_cli
import re

stocks = [x for x in mongo_cli.get_database('wfp_com_patent').collection_names(include_system_collections=False) if
          re.match('^\d{6}$', x)]

db = mongo_cli.get_database('wfp_com_patent')

sum_green = 0
for stock in stocks:
    num = db.get_collection(stock).count({'green_num': {'$gt': 0}})
    sum_green += num

print(sum_green)

from pymongo import MongoClient
import re

"""
查询并删除专利数为0的
"""
client = MongoClient(host='10.170.47.245', port=27017)
db = client.get_database('wfp_com_patent')
desc = db.get_collection('description')
cnt = 0

rs = []
for x in desc.find():
    if x['total'] > 0:
        continue
    stock_code = x['stockCode']
    cnt += 1
    print('%d\t%s\t%d' % (cnt, stock_code, x['total']))
    db.get_collection(stock_code).drop()
    rs.append(stock_code)

if len(rs) > 0:
    desc.delete_many({
        'stockCode': {
            "$in": rs
        }
    })

from pymongo import MongoClient

client = MongoClient(host='10.170.47.245', port=27017)
collection = client.get_database('wfp_com_patent').get_collection('description')

stock = {}

for x in collection.find():
    stock_code = x['stockCode']
    if stock_code in stock:
        stock[stock_code] += 1
    else:
        stock[stock_code] = 1

[print(k, v) for k, v in stock.items() if v > 1]

#
# for x in collection.find():
#     stock_code = x['stockCode']
#     if stock_code in stock:
#         collection.delete_one({"_id": x['_id']})
#     else:
#         stock.add(stock_code)

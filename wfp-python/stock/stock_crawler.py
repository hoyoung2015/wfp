import requests
from pymongo import MongoClient

client = MongoClient(host='127.0.0.1', port=27017)
db_name = 'wfp'
collection_name = 'web_source'
collection = client.get_database(db_name).get_collection(collection_name)

URL = 'http://stockdata.stock.hexun.com/2009_sdgd_%s.shtml'

headers = {
    'User-Agent': 'Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36'
}

for cur in collection.find({}, projection={'stockCode': 1}):
    stock_code = cur['stockCode']
    target_url = URL % stock_code
    print(target_url)
    resp = requests.get(url=target_url, headers=headers)
    print(resp.text)
    break

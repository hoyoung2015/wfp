__author__ = 'v_huyang01'
import pymongo
import urllib


def get_domain(url):
    proto, rest=urllib.splittype(url)
    res,rest = urllib.splithost(rest)
    return res

host = 'localhost'
port = 27017
client = pymongo.MongoClient(host,port);
db = client.get_database('wfp')
db_news = db.get_collection('new_item')
domain = set()
for item in db_news.find().limit(10000):
    domain.add(get_domain(item['targetUrl']))

count_list = list()
for d in domain:
    count_list.append((
        d,db_news.count({
            "targetUrl":{
                "$regex":d
            }
        })
    ))
count_list = sorted(count_list,key=lambda cnt:cnt[1],reverse=True)
f = file('news_domain.txt','w+')
for dc in count_list:
    print(dc)
    f.write('%s,%d\n' % dc)
f.close()

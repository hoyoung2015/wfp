# -*- coding: utf-8 -*-
"""
从nutch采集的mongodb库中导出外部链接的域名
"""
import sys
import urllib
import pymongo
import getopt

def usage():
	print '-h host'
	print '-p prot'
	print '-d domain'
	print '-o output'
if len(sys.argv)==1:
	usage()
	sys.exit()

opts, args = getopt.getopt(sys.argv[1:], "h:p:d:c:o:")

host = "127.0.0.1"
port = "27017"
output = ""
domain = ""
collection = ""
for op, value in opts:
    if op == "-h":
        host = value
    elif op == "-p":
        port = value
    elif op == "-d":
        domain = value
    elif op == "-o":
        output = value
    elif op == "-c":
        collection = value
#print host
#print port
#print output
#print domain

reload(sys)
sys.setdefaultencoding('utf-8')

client = pymongo.MongoClient(host, int(port))
db = client.nutch
db_collection = db[collection]
extlinks = dict()
for item in db_collection.find():
    if item.has_key('extlinks'):
        for (k_url,v_name) in item['extlinks'].items():
            extlinks[k_url] = v_name

indomains = set()           
outdomains = set()           
#domain = 'dfpv.com.cn'            
for url,v_name in extlinks.items():
    #print url
    proto, rest=urllib.splittype(url)
    res, rest = urllib.splithost(rest) 
    res = res.replace('·','.')
    #print res
    if domain in res:
        indomains.add(res)
    else:
        outdomains.add(res)
if len(outdomains)==0:
    print 'There is no outdomains.'
    sys.exit()

#输出外部域名
f = file(output+'_o','w+')
for d in outdomains:
    f.write(d+'\n')
f.close()

#输出内部域名
f = file(output+'_i','w+')
for d in indomains:
    f.write(d+'\n')
f.close()

print 'urls:',len(outdomains)
print 'output:',output
print 'successful'
        
        



    

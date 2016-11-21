# -*- coding: utf-8 -*-
"""
从nutch采集的mongodb库中导出外部链接的域名
"""
import sys
import urllib
from common.mongo import mongo_cli


if __name__=='__main__':
    output = 'external_links.txt'
    domain = 'dfpv.com.cn'
    db_collection = mongo_cli.get_database('nutch').get_collection('website')
    extlinks = dict()
    for item in db_collection.find():
        if item.has_key('extlinks'):
            for (k_url,v_name) in item['extlinks'].items():
                extlinks[k_url] = v_name

    indomains = set()
    outdomains = set()
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
        print('There is no outdomains.')
        sys.exit()

    #输出外部域名
    f = open(output+'_o','w')
    for d in outdomains:
        f.write(d+'\n')
    f.close()

    #输出内部域名
    f = open(output+'_i','w')
    for d in indomains:
        f.write(d+'\n')
    f.close()

    print('urls:',len(outdomains))
    print('output:',output)
    print('successful')
        
        



    

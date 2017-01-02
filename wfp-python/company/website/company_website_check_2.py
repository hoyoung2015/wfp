# -*- coding: utf-8 -*-
"""
检测网站是否可访问
"""
__author__ = 'huyang'
from common.mongo import mongo_cli
from threadpool import ThreadPool, makeRequests, WorkRequest
from urllib import request


def checkWebsiteAvl(stockCode, name, webSite):

    try:
        req = request.Request(url=webSite, headers={
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'zh-CN,zh;q=0.8',
            'Cache-Control': 'max-age=0',
            'User-Agent': 'Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36'
        })
        f = request.urlopen(req, timeout=30)
        print('%s status:%d' % (webSite, f.status))
    except:
        # traceback.print_exc()
        print('%s-%s not access' % (stockCode, webSite))
    finally:
        pass


if __name__ == "__main__":
    wfp = mongo_cli.get_database('wfp')
    company_info = wfp.get_collection('company_info')
    web_site_not_exists = list()
    web_site_format_error = list()
    executor_pool = ThreadPool(10)
    list_of_args = [(com['stockCode'], com['name'], com['webSite']) for com in
                    company_info.find({'is_target': 1}, {'_id': 0})]
    [executor_pool.putRequest(WorkRequest(checkWebsiteAvl, x)) for x in list_of_args]
    executor_pool.wait()
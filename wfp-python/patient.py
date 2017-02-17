# -*- coding: utf-8 -*-
"""
专利爬虫
爬取http://www.innojoy.com/
"""
import logging
from common import log
import requests
import time
from baidu_crawler import user_agents

logger = log.get_logger(filename='patient.log', level=logging.INFO)


def process_doc(doc):
    total = 0
    if doc is None:
        return
    if len(doc['Option']['PatentList']) == 0:
        return
    for item in doc['Option']['PatentList']:
        total += 1
        print(str(total) + '\t' + item['TI'].split('\n')[0])


def crawl(stock_code, com_full_name):
    post_url = 'http://www.innojoy.com/client/interface.aspx'
    PAGE_SIZE = 50
    SLEEP_TIME = 3
    headers = {
        'Accept': 'application/json, text/javascript, */*; q=0.01',
        'Content-Type': 'application/json',
        'Host': 'www.innojoy.com',
        'Origin': 'http://www.innojoy.com',
        'Referer': 'http://www.innojoy.com/searchresult/default.html',
        'User-Agent': user_agents.get_random_pc_agent(),
        'X-Requested-With': 'XMLHttpRequest'
    }
    param = {
        "requestModule": "PatentSearch",
        "userId": "",
        "patentSearchConfig": {
            "Query": "AS=" + com_full_name,
            "TreeQuery": "",
            "Database": "wgzl,syxx,fmzl",
            "Action": "Search",
            "DBOnly": 0,
            "Page": 1,
            "PageSize": PAGE_SIZE,
            "GUID": "",
            "Sortby": "-IDX,PNM",
            "AddOnes": "",
            "DelOnes": "",
            "RemoveOnes": "",
            "SmartSearch": "",
            "TrsField": ""
        }
    }
    MAX_RETRY_TIME = 3
    running = True
    retry_time = 0
    while running and retry_time < MAX_RETRY_TIME:
        try:
            resp = requests.post(post_url, data=None, json=param, headers=headers, proxies={
                'http': 'http://124.88.67.19:80'
            }, timeout=40)
            doc = resp.json()
            if doc['ReturnValue'] != 0:
                if doc['ErrorInfo'] == 'page number unexpected.':
                    print('page number unexpected')
                    # 没有这一页了
                    running = False
                    break
                elif doc['ErrorInfo'] == '您IP已超过今天的阅读量，非常感谢您的厚爱，请联系升级到VIP帐号，或请您明天再访问！':
                    print('您IP已超过今天的阅读量，非常感谢您的厚爱，请联系升级到VIP帐号，或请您明天再访问！')
                    retry_time += 1
                else:
                    print(doc)
                    retry_time += 1
            else:
                doc['StockCode'] = stock_code
                process_doc(doc)
                retry_time = 0

                if len(doc['Option']['PatentList']) < PAGE_SIZE or doc['Option']['GUID'].startswith('NONE'):
                    # 到末页了
                    running = False
                    break
                # 下一页
                param['patentSearchConfig']['GUID'] = doc['Option']['GUID']
                param['patentSearchConfig']['Page'] += 1
        except Exception as e:
            retry_time += 1
            logger.warn(e)
        if running:
            time.sleep(SLEEP_TIME)

    if retry_time == MAX_RETRY_TIME:
        logger.warn(com_full_name + '(' + stock_code + ')重试' + str(MAX_RETRY_TIME) + '后失败')


if __name__ == "__main__":
    crawl('111111', '中国神华能源股份有限公司')

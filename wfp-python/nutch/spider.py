# -*- coding: utf-8 -*-
"""

"""
import sys
import os
import re

NUTCH_HOME = '/home/hoyoung/workspace/eclipse/nutch-wfp/runtime/local'
TMP_PATH = '/home/hoyoung/workspace/wfp/wfp-python/.tmp'
COMPANY_PATH = '/home/hoyoung/workspace/wfp/wfp-python/nutch/website_list'

JOB_NUM = 3


class CompanySimple:
    domain_pattern = 'http(s)?://(www\.)?'

    def __init__(self, stockCode, sname, webSite):
        self.stockCode = stockCode
        self.sname = sname
        self.webSite = webSite

    def __str__(self, *args, **kwargs):
        return self.stockCode + '\t' + self.sname + '\t' + self.webSite

    def getDomain(self):
        s = re.sub('http(s)?://(www\.)?', '', self.webSite)
        idx = s.find('/')
        if idx > 0:
            s = s[:idx]
        return s


def get_company_info(company_path):
    with open(company_path) as f:
        lines = f.readlines()
    coms = list()
    for line in lines:
        if line.endswith('\n'):
            line = line[:-1]
        arr = line.split('\t')
        coms.append(CompanySimple(arr[0], arr[1], arr[2]))
    return coms


if __name__ == '__main__':
    if not os.path.exists(TMP_PATH):
        os.makedirs(TMP_PATH)
    conf_dirs = list()
    url_dirs = list()
    for i in range(JOB_NUM):
        conf_dir = TMP_PATH + '/conf' + str(i)
        # make conf dir
        if not os.path.exists(conf_dir):
            cmd = 'cp -R %s %s' % (NUTCH_HOME + '/conf', conf_dir)
            print('excute command:' + cmd)
            if os.system(cmd) != 0:
                continue
        # make url dir
        url_dir = TMP_PATH + '/url' + str(i)
        if not os.path.exists(url_dir):
            os.makedirs(url_dir)
        url_dirs.append(url_dir)
        conf_dirs.append(conf_dir)
    companies = get_company_info(COMPANY_PATH + '/site03.txt')
    company = companies[0]
    print(company)
    cur_conf_dir = conf_dirs[0]
    cur_url_dir = url_dirs[0]

    with open(cur_url_dir + '/seeds.txt', 'w+') as f:
        f.write(company.webSite)
    print('write domain '+company.getDomain()+' to ' + cur_conf_dir + '/domain-urlfilter.txt')
    with open(cur_conf_dir + '/domain-urlfilter.txt', 'w+') as f:
        f.write(company.getDomain())
    # set environment variable
    os.environ['NUTCH_CONF_DIR'] = cur_conf_dir

    crawl_cmd = '%s/bin/crawl %s %s 99999999' % (NUTCH_HOME,cur_url_dir,company.stockCode)
    print(crawl_cmd)
    os.system(crawl_cmd)

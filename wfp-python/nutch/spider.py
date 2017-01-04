# -*- coding: utf-8 -*-
"""

"""
import sys
import os
import re
from common.mongo import mongo_cli
from common.log import logger
from utils.EmailSender import EmailSender

NUTCH_HOME = '/home/hoyoung/workspace/eclipse/nutch-wfp/runtime/local'
TMP_PATH = '/home/hoyoung/workspace/wfp/wfp-python/.tmp'
LOG_PATH = '/home/hoyoung/tmp/nutch/logs'
COMPANY_FILE = '/home/hoyoung/workspace/wfp/wfp-python/nutch/website_list/site03.txt'

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


def set_hadoop_log_path(log4j_file, hadoop_log_dir, hadoop_log_file):
    with open(log4j_file, 'r+') as f:
        lines = f.readlines()
    lines[16] = 'hadoop.log.dir=' + hadoop_log_dir + '\n'
    lines[17] = 'hadoop.log.file=' + hadoop_log_file + '\n'
    with open(log4j_file, 'w+') as f:
        f.writelines(lines)


if __name__ == '__main__':
    if not os.path.exists(LOG_PATH):
        os.makedirs(LOG_PATH)
    email_sender = EmailSender('smtp.sina.com.cn', 'hoyoung@sina.cn', mail_pass='')
    nutch_db = mongo_cli.get_database('nutch')
    if not os.path.exists(TMP_PATH):
        os.makedirs(TMP_PATH)
    conf_dirs = list()
    url_dirs = list()
    for i in range(JOB_NUM):
        conf_dir = TMP_PATH + '/conf' + str(i)
        # make conf dir
        if not os.path.exists(conf_dir):
            cmd = 'cp -R %s %s' % (NUTCH_HOME + '/conf', conf_dir)
            logger.info('excute command:' + cmd)
            if os.system(cmd) != 0:
                continue
        # make url dir
        url_dir = TMP_PATH + '/url' + str(i)
        if not os.path.exists(url_dir):
            os.makedirs(url_dir)
        url_dirs.append(url_dir)
        conf_dirs.append(conf_dir)
    companies = get_company_info(COMPANY_FILE)

    for company in companies:
        logger.info('start to crawl [%s]' % company)
        # check collection exists
        if company.stockCode + '_webpage' in nutch_db.collection_names():
            logger.info('company %s[%s] has been crawled' % (company.sname, company.stockCode))
            continue
        cur_conf_dir = conf_dirs[0]
        cur_url_dir = url_dirs[0]
        # set log4j.properties hadoop.log.path
        set_hadoop_log_path(cur_conf_dir + '/log4j.properties', LOG_PATH, company.stockCode + '.log')
        # write seeds
        with open(cur_url_dir + '/seeds.txt', 'w+') as f:
            f.write(company.webSite)
        logger.info('write domain ' + company.getDomain() + ' to ' + cur_conf_dir + '/domain-urlfilter.txt')
        # write domain-urlfilter
        with open(cur_conf_dir + '/domain-urlfilter.txt', 'w+') as f:
            logger.info('[%s] domain %s' % (company, company.getDomain()))
            f.write(company.getDomain())
        # set environment variable
        os.environ['NUTCH_CONF_DIR'] = cur_conf_dir
        crawl_cmd = '%s/bin/crawl %s %s 99999999' % (NUTCH_HOME, cur_url_dir, company.stockCode)
        logger.info(crawl_cmd)

        if os.system(crawl_cmd) == 0:
            logger.info('crawl task successful [%s] ' % company)

        else:
            logger.warn('crawl task occur an error [%s]' % company)
            subject = 'company[%s] crawl error' % company.sname
            content = 'company[%s] crawl error' % company
            email_sender.send_text('hoyoung@sina.cn', 'huyang09@baidu.com', subject, content)
            break

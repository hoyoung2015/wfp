# -*- coding: utf-8 -*-
"""
代理池
"""
import requests
from lxml import etree
import time
from multiprocessing.pool import ThreadPool
from baidu_crawler import user_agents
from common import currentlog
from common import log
import logging


class IpPool(object):
    _logger = currentlog.get_current_logger(name='ip_pool', filename='../log/ip_pool.log', level=logging.DEBUG)
    # _logger = log.get_logger(name='ip_pool', filename='../ip_pool2.log', level=logging.DEBUG)
    _url_xpath = '//table[@id="ip_list"]//tr[position()>1]/td[2]/text()'
    _port_xpath = '//table[@id="ip_list"]//tr[position()>1]/td[3]/text()'

    def _get_proxies_from_site(self, current_page):
        url = 'http://www.xicidaili.com/nn/' + str(current_page)
        self._logger.info("start crawl url " + url)
        headers = {
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'zh-CN,zh;q=0.8',
            'Cache-Control': 'max-age=0',
            'User-Agent': user_agents.get_random_pc_agent()
        }
        results = requests.get(url, headers=headers)

        tree = etree.HTML(results.text)

        url_results = tree.xpath(self._url_xpath)  # Get ip

        port_results = tree.xpath(self._port_xpath)  # Get port
        urls = [line.strip() for line in url_results]
        ports = [line.strip() for line in port_results]

        ip_list = []
        if len(urls) != len(ports):
            self._logger.warn("No! It's crazy!")
        else:
            ip_list = [urls[i] + ":" + ports[i] for i in range(len(urls))]
        return ip_list

    # Get all ip in 0~page pages website
    def get_all_ip(self, page):
        ip_crawl_threadpool = ThreadPool(20)
        results = [ip_crawl_threadpool.apply_async(self._get_proxies_from_site, args=(i + 1,)) for i in range(page)]
        ip_crawl_threadpool.close()
        ip_crawl_threadpool.join()
        all_ip = []
        # 合并抓取到的ip
        for x in results:
            all_ip[0:0] = x.get()
        return all_ip

    # Use http://lwons.com/wx to test if the server is available.
    def _valid_proxy(self, p, timeout):
        url = 'http://www.baidu.com/img/baidu_jgylogo3.gif'
        proxy = {'http': 'http://' + p}
        succeed = False
        headers = {'User-Agent': user_agents.get_random_pc_agent()}
        try:
            r = requests.get(url, proxies=proxy, timeout=timeout, headers=headers)
            if r.status_code == 200:
                succeed = True
        except Exception as e:
            self._logger.debug('error:' + p)
            succeed = False
        return succeed and p or None

    def _get_valid_proxies(self, proxies, timeout):
        # create thread pool
        executor_pool = ThreadPool(20)
        results = [executor_pool.apply_async(self._valid_proxy, args=(p, timeout)) for p in set(proxies)]
        executor_pool.close()
        executor_pool.join()
        return [x.get() for x in results if x.get() is not None]

    def get_the_best(self, round, page, timeout, sleeptime):
        ip_port_list = self.get_all_ip(page)
        self._logger.info('got %d ip' % len(ip_port_list))
        for i in range(round):
            self._logger.debug('round ' + str(i + 1))
            proxies = self._get_valid_proxies(ip_port_list, timeout)
            if i != round - 1:
                time.sleep(sleeptime)
        return proxies


if __name__ == '__main__':
    ip_pool = IpPool()
    # ip_list = ip_pool.get_all_ip(50)
    # print(len(ip_list))
    # with open('ip_list.txt', 'w+') as f:
    #     f.write('\n'.join(ip_list))
    #     f.close()
    proxies = ip_pool.get_the_best(round=1, page=1, timeout=10, sleeptime=60)
    with open('ip_list.txt', 'w+') as f:
        f.write('\n'.join(proxies))
        f.close()
    print(len(proxies))

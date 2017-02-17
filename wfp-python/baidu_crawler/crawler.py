# -*- coding: utf-8 -*-


from baidu_crawler.ip_pool import IpPool


class BaiduCrawler(object):
    avail_proxies = set()

    baidu_page_url = set()

    # 抓取线程
    fetch_thread = 1

    def __init__(self):
        # 初始化ip代理池
        self.ip_pool = IpPool()

    def _start_ip_pool_task(self):
        pass

    def crawl(self, keyword):
        pass


import requests
"""
106.46.136.71:808
220.166.241.78:8118
106.46.136.115:808
111.76.133.172:808
122.189.27.242:8118
111.76.133.106:808
125.118.149.209:808
110.72.1.111:8123
110.7.159.164:8118
106.46.136.46:808
106.46.136.98:808
113.18.193.6:80
123.145.84.30:8118
122.228.179.178:80
171.39.32.250:8123
60.206.32.250:8998
123.57.150.43:80
60.185.233.185:808
180.95.172.219:80
111.76.133.177:808
202.108.2.42:80
111.76.133.159:808
183.7.182.241:8118
180.123.67.169:8118
111.76.129.11:808
106.46.136.34:808

url = 'http://api.zdaye.com/?api=201701111914116028&pw=hoyoung&gb=3&rtype=1'
        resp = requests.get(url)
        return resp.text.split('\r\n')
"""

url = 'http://api.zdaye.com/?api=201701111914116028&pw=hoyoung&rtype=1'
resp = requests.get(url)

print(resp.text.split('\r\n'))


def _get_proxies_from_site(self, current_page):
    url = 'http://www.66ip.cn/areaindex_1/' + str(current_page) + '.html'  # The ip resources url
    url_xpath = '/html/body/div[last()]//table//tr[position()>1]/td[1]/text()'
    port_xpath = '/html/body/div[last()]//table//tr[position()>1]/td[2]/text()'
    headers = {
        'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
        'Accept-Language': 'zh-CN,zh;q=0.8',
        'Cache-Control': 'max-age=0',
        'User-Agent': user_agents.get_random_agent()
    }
    results = requests.get(url, headers=headers)
    tree = etree.HTML(results.text)
    url_results = tree.xpath(url_xpath)  # Get ip
    port_results = tree.xpath(port_xpath)  # Get port
    urls = [line.strip() for line in url_results]
    ports = [line.strip() for line in port_results]

    ip_list = []
    if len(urls) != len(ports):
        self.logger.info("No! It's crazy!")
    else:
        for i in range(len(urls)):
            # Match each ip with it's port
            full_ip = urls[i] + ":" + ports[i]
            self.logger.debug(full_ip)
            ip_list.append(full_ip)
    return ip_list
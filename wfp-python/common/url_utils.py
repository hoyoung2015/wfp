from urllib.parse import urlparse


# 从url中获取域名
def get_domain(url):
    r = urlparse(url)
    return r.netloc

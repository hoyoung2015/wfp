import requests


url = "http://www.ggec.com.cn/downFile/?fn=http://www.cninfo.com.cn/cninfo-new/disclosure/szse_sme/bulletin_detail/true/1202797911?announceTime=2016-10-28&newName=2016%E5%B9%B4%E7%AC%AC%E4%B8%89%E5%AD%A3%E5%BA%A6%E6%8A%A5%E5%91%8A%E6%AD%A3%E6%96%87"

resp = requests.get(url)

print(resp.text)
import requests

url = 'https://wenzhi.api.qcloud.com/v2/index.php'
params = {
    'Action': 'TextSentiment',
    'Nonce': '345122',
    'Region': 'sz',
    'SecretId': 'AKIDz8krbsJ5yKBZQpn74WFkmLPx3gnPhESA',
    'Timestamp': '1408704141',
    'Signature': 'HgIYOPcx5lN6gz8JsCFBNAWp2oQ',
    'content': '双万兆服务器就是好，只是内存小点'
}
resp = requests.get(url, params=params)


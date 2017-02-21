from urllib.parse import urlparse
# rds = Redis(host='192.168.1.110', port=6379, db=0, decode_responses=True)
# key = 'test'
# data = rds.lpop(key)
# print(data)
# rds.rpush(key, data)
# print(rds.lpop(key))

url = 'http://www.sanjin.com.cn/jkzx/club.do?act=selectUserClubById&currentpage=13&un=sanjinsy&clubid=81&sx=null&usid=488&qx=3,-1'
r = urlparse(url)
print(r.netloc)

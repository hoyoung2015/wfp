import re

p = re.compile('http[s]?://')
s = 'http://www.xjjxy.com.cn'
s= re.sub('http(s)?://(www\.)?','',s)
print(s)
idx = s.find('/')
if idx>0:
    s = s[:idx]
print(s)


print('aa\n'.endswith('\n'))
print('aa'.endswith('\n'))
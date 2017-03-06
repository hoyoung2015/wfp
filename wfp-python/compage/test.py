from pymongo import MongoClient
import re

client = MongoClient(host='10.170.61.54', port=27017)

db = client.get_database('wfp_com_page')
col_set = set()
for name in db.collection_names():
    if re.match('^\d{6}$', name):
        col_set.add(name)
client.close()

cnt = 0
total = 0;


def write_line(s):
    with open('ttt.txt', 'a') as f:
        f.write(s + '\n')
        f.close()


with open('web_source_crawl.txt') as f:
    for line in f.readlines():
        line = line.strip().replace('\n', '')
        if line == '' or line.startswith('#'):
            continue
        total += 1
        sp = line.split('\t')
        if sp[0] in col_set:
            cnt += 1
        else:
            write_line(line)
    f.close()

print(cnt, '/', total)

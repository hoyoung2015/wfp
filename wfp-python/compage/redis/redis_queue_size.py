import os
import sys
from redis import Redis

sys.path.append(rootPath)


host = '127.0.0.1'
host = '10.173.7.35'
rds = Redis(host, port=6379, db=0, decode_responses=True)

[print(rds.llen(x), '\t', x) for x in rds.keys(pattern='queue_*')]

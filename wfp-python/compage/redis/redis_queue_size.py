from redis import Redis

host = '127.0.0.1'
host = '10.173.244.157'
rds = Redis(host, port=6379, db=0, decode_responses=True)

[print(rds.llen(x), '\t', x) for x in rds.keys(pattern='queue_*')]

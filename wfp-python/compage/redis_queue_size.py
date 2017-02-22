from redis import Redis

rds = Redis(host='10.170.34.130', port=6379, db=0, decode_responses=True)

[print(rds.llen(x), '\t', x) for x in rds.keys(pattern='queue_*')]

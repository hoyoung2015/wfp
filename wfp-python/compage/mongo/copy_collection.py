from common.mongo import copy_collection


if __name__ == '__main__':
    copy_collection(src={
        'db': 'tmp',
        'host': '10.170.47.192',
        'port': 27017
    }, dist={
        'db': 'tmp',
        'host': '127.0.0.1',
        'port': 27017
    }, collection={
        'sha1_distinct': 'sha1_distinct'
    })

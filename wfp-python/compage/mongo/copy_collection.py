from common.mongo import copy_collection


if __name__ == '__main__':
    copy_collection(src={
        'db': 'wfp',
        'host': '127.0.0.1',
        'port': 27017
    }, dist={
        'db': 'wfp',
        'host': '127.0.0.1',
        'port': 27017
    }, collection={
        'footprint': 'footprint_bak'
    })

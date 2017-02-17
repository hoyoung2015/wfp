# -*- coding: utf-8 -*-
from common.mongo import mongo_cli


def process(url):

    pass
[print(x) for x in mongo_cli.get_database('wfp_spider').get_collection('com_page').find({},{"stockCode":1,"url":1,"_id":0})]

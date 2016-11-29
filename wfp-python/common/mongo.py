__author__ = 'huyang'
from common import config
from pymongo import MongoClient
import logging


mongo_cli = MongoClient(host=config.get('mongodb','host'),port=config.getint('mongodb','port'))

if __name__=='__main__':
    logger = logging.getLogger("common mongo")
    logger.info("test"*10)
    # wfp = mongo_cli.get_database('wfp')
    # company_info = wfp.get_collection('company_info')
    # for x in company_info.find():
    #     print(x)
__author__ = 'huyang'
import configparser
import logging
import logging.config
# 配置文件
config = configparser.ConfigParser()
config.read(filenames='../config.conf')
# 日志
logging.config.fileConfig("../logging.conf")



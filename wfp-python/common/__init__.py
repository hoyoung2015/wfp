# -*- coding: utf-8 -*-
"""
公共模块
"""
__author__ = 'huyang'
import os
import configparser
import logging.config

common_path = os.path.split(os.path.realpath(__file__))[0]+'/../'
# print(common_path)
# 配置文件
config = configparser.ConfigParser()
config.read(filenames=common_path+'config.conf')
# 日志
# logging.config.fileConfig(common_path+"logging.conf")




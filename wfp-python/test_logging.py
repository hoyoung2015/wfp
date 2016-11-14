__author__ = 'v_huyang01'
import logging
import logging.config

logging.config.fileConfig("logging.conf")
logger = logging.getLogger('main')

logger.debug("main logging")
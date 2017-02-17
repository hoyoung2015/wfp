from common import currentlog
import logging
from multiprocessing.pool import ThreadPool
import time

logger = currentlog.get_current_logger(filename='../wfp.log', level=logging.DEBUG)
logger.info("test currentlog")


def fun1(x):
    print('fun1', x)
    logger.info("fun1 log")
    time.sleep(1)


def fun2(x):
    print(x)
    logger.info("fun2 log")
    time.sleep('fun2', 2)


if __name__ == '__main__':
    pool = ThreadPool(2)
    pool.apply_async(fun1, (1,))
    pool.apply_async(fun2, (2,))
    pool.close()
    pool.join()

import xlrd
from pymongo import MongoClient
from selenium import webdriver
from multiprocessing.pool import ThreadPool
from selenium.webdriver.firefox.firefox_profile import FirefoxProfile


def get_firefox_profile():
    ## get the Firefox profile object
    firefoxProfile = FirefoxProfile()
    ## Disable CSS
    firefoxProfile.set_preference('permissions.default.stylesheet', 2)
    ## Disable images
    firefoxProfile.set_preference('permissions.default.image', 2)
    ## Disable Flash
    firefoxProfile.set_preference('dom.ipc.plugins.enabled.libflashplayer.so', 'false')


num = 0


def check(stock_code, url):
    global num
    browser = webdriver.Firefox(executable_path='/Users/baidu/local/bin/geckodriver',
                                firefox_profile=get_firefox_profile())
    browser.set_window_size(width=1000, height=500)
    browser.set_page_load_timeout(time_to_wait=40)

    num += 1
    print('%d\t%s\t%s' % (num, stock_code, url))
    try:
        browser.get(url)
        if '<!--由中企动力科技集团股份有限公司' in browser.page_source:
            return {'stockCode': stock_code, 'target': 1}
        return {'stockCode': stock_code, 'target': 0}
    except Exception as e:
        print(e, stock_code)
        return
    finally:
        browser.quit()


if __name__ == '__main__':
    filename = '/Users/baidu/Documents/web_source.xlsx'
    data = xlrd.open_workbook(filename)  # 打开xls文件
    table = data.sheets()[0]  # 打开第一张表
    nrows = table.nrows  # 获取表的行数
    m = {}

    for i in range(nrows):  # 循环逐行打印
        m[table.row_values(i)[0]] = table.row_values(i)[2]
    client = MongoClient(host='127.0.0.1', port=27017)
    try:
        db = client.get_database('tmp')
        collection_zhongqidongli = db.get_collection('zhongqidongli')
        executor_pool = ThreadPool(5)
        rs = []
        for stock_code, url in m.items():
            if collection_zhongqidongli.count({'stockCode': stock_code}) > 0:
                continue
            if url.startswith('https'):
                continue
            rs.append(executor_pool.apply_async(check, args=(stock_code, url)))
            # break
        executor_pool.close()
        executor_pool.join()
        [collection_zhongqidongli.insert_one(x.get()) for x in rs if x.get() is not None]
    finally:
        client.close()

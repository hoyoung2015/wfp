from selenium import webdriver
from multiprocessing.pool import ThreadPool

num = 0


def read_proxy(filename):
    with open(filename) as f:
        for line in f.readlines():
            line = line.strip().replace('\n', '')
            if line == '' or line.startswith('#'):
                continue
        f.close()


def check(item={}):
    global num
    browser = webdriver.Firefox(executable_path='/Users/baidu/local/bin/geckodriver')
    browser.set_window_size(width=1000, height=500)
    browser.set_page_load_timeout(time_to_wait=30)
    url = item['url']
    if url is None or url == '':
        return
    try:
        browser.get(url)
    except Exception as e:
        print(e, item)
    finally:
        final_url = browser.current_url
        browser.quit()
    num += 1
    print(num, '\t', url, '\t', final_url)
    if final_url is not None and final_url != '' and final_url != 'about:blank':
        return '%s\t%s\t%s\t%d' % (item['stockCode'], item['sname'], final_url, item['sleepTime'])


def get_com_source(data_file_path):
    rs = []
    with open(data_file_path) as f:
        for line in f.readlines():
            line = line.strip().replace('\n', '')
            if line == '' or line.startswith('#'):
                continue
            arr = line.split('\t')
            rs.append({
                'stockCode': arr[0],
                'sname': arr[1],
                'url': arr[2],
                'sleepTime': int(arr[3])
            })
        f.close()
    return rs


if __name__ == '__main__':
    executor_pool = ThreadPool(5)
    results = [executor_pool.apply_async(check, args=(item,)) for item in get_com_source('web_source_sate_dog.txt')]
    executor_pool.close()
    executor_pool.join()

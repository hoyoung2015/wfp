from selenium import webdriver

browser = webdriver.Firefox(executable_path='/Users/baidu/local/bin/geckodriver')
browser.set_window_size(width=1100, height=770)

browser.get(url='http://www.xdect.com.cn')

print(browser.title)


browser.quit()
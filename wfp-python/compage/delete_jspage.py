import requests
import xlrd
from common import user_agents

filename = '/Users/baidu/Documents/web_source.xlsx'
data = xlrd.open_workbook(filename)  # 打开xls文件

table = data.sheets()[0]  # 打开第一张表
nrows = table.nrows  # 获取表的行数
m = {}
for i in range(nrows):  # 循环逐行打印
    m[table.row_values(i)[0]] = table.row_values(i)[2]

flag_str = '<!--由中企动力科技集团股份有限公司'

fuck = set(
    ['002438', '300539', '603566', '002598', '000590', '002669', '002452', '002723', '600127', '002295', '002476',
     '600367'])

# print(fuck)


for stock_code, url in m.items():
    if stock_code in fuck:
        continue
    if url.startswith('https'):
        print(stock_code)
        continue
    headers = {'User-Agent': user_agents.get_random_pc_agent()}
    try:
        resp = requests.get(url, headers=headers, timeout=20)
        if resp.status_code == 200:
            if flag_str in resp.text:
                print(stock_code, url)
            if '捷瑞数字' in resp.text:
                print(stock_code, url, '捷瑞数字')
        else:
            pass
    except Exception as e:
        print(e)
        print(stock_code, url)

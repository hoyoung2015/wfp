# -*- coding: utf-8 -*-
import xlrd

data = xlrd.open_workbook('../data/word_meta.xlsx')  # 打开xls文件
table = data.sheets()[0]  # 打开第一张表
nrows = table.nrows  # 获取表的行数
m = {}
for i in range(nrows):  # 循环逐行打印
    word = table.row_values(i)[3]
    word = word.replace(',', '|')
    m[table.row_values(i)[1]] = word

for k, v in m.items():
    print(k, '\t', v)

# -*- coding: utf-8 -*-
"""
对责任报告进行分词
http://stockdata.stock.hexun.com/zrbg/
"""
import jieba
import os
from collections import OrderedDict

dir = "/home/hoyoung/vm_share/txt/"

wordmap = {}
count = 1
for file in os.listdir(dir):
    content = open(dir+file).read()
    list = jieba.cut(content, cut_all=True, HMM=True)
    for word in list:
        if word in wordmap:
            wordmap[word] = wordmap[word]+1
        else:
            wordmap[word] = 1

    count += 1
    if count>100:
        break

d = OrderedDict(wordmap)
bar = OrderedDict(sorted(d.items(), key=lambda x: x[1]))
for key in bar.keys():
    print(key+":"+str(bar[key]))


# content = open("/home/hoyoung/vm_share/txt/2015_三钢闽光_002110.txt").read()
# list = jieba.cut(content,cut_all=True,HMM=True)
# print("\n".join(list))
# -*- coding: utf-8 -*-
"""
对责任报告进行分词
http://stockdata.stock.hexun.com/zrbg/
"""
import jieba
import os
import re
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

    print(count)
    count += 1
    # if count>100:
    #     break

d = OrderedDict(wordmap)
bar = OrderedDict(sorted(d.items(), key=lambda x: x[1]))


print("read stopwords")
stopwords_list = open("stopwords.txt","r").readlines()
stopwords_set = set()
for line in stopwords_list:
    stopwords_set.add(line.strip('\n'))
print(stopwords_set)
wfile = open("words.txt","w")
for key in bar.keys():
    # 过滤停用词
    if key in stopwords_set or re.match("[+-]?\d+$",key)!=None:
        continue
    wfile.writelines(key+"\t"+str(bar[key])+"\n")
    print(key+"\t"+str(bar[key]))


# content = open("/home/hoyoung/vm_share/txt/2015_三钢闽光_002110.txt").read()
# list = jieba.cut(content,cut_all=True,HMM=True)
# print("\n".join(list))
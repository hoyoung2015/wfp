# -*- coding: utf-8 -*-
"""
对责任报告进行分词
http://stockdata.stock.hexun.com/zrbg/
"""
import jieba
import os
import re
from collections import OrderedDict

dir = "/Users/baidu/tmp/zrbg_txt_2010/"

wordmap = {}
count = 1
list_files = [f for f in os.listdir(dir) if f.startswith('.') is False]
total = len(list_files)
cnt = 0

for file in list_files:
    cnt += 1
    print('\r%d/%d\t%s' % (cnt, total, file), end='')
    content = open(dir + file).read()
    word_list = jieba.cut(content, cut_all=False)
    for word in word_list:
        word = word.strip()
        if word == '' or len(word) == 1:
            continue
        if word in wordmap:
            wordmap[word] += 1
        else:
            wordmap[word] = 1

total_words_num = sum(wordmap.values())

d = OrderedDict(wordmap)
bar = OrderedDict(sorted(d.items(), key=lambda x: x[1], reverse=True))
print()
print("read stopwords")
stopwords_list = open("stopwords.txt", "r").readlines()
stopwords_set = set()
for line in stopwords_list:
    stopwords_set.add(line.strip('\n'))

with open("word_seg_result_freq.txt", "w") as word_seg_result:
    for key in bar.keys():
        # 过滤停用词
        if key in stopwords_set or re.match("[+-]?\d+$", key) or re.match('[\d\.\?\-]+', key) or wordmap[key] <= 1:
            continue
        word_seg_result.writelines(key + "\t" + str(bar[key]) + "\t" + str(bar[key] / total_words_num) + "\n")
    word_seg_result.close()

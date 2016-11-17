# -*- coding: utf-8 -*-

stopwords_list = open("stopwords.txt","r").readlines()

stopwords_set = set()
for line in stopwords_list:
    stopwords_set.add(line.strip('\n'))

print(stopwords_set)
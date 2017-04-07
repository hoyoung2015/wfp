# -*- coding: utf-8 -*-
import os
import jieba
from nltk.corpus import stopwords
from sklearn.feature_extraction.text import CountVectorizer

stopwords_set = set(stopwords.words('chinese'))
stopwords_set.add(' ')
stopwords_set.add('')


def get_file_list(path):
    filelist = []
    files = os.listdir(path)
    for f in files:
        if f[0] == '.':
            pass
        else:
            filelist.append(f)
    return filelist, path


def word_seg(filename, path, seg_path):
    f = open(path + "/" + filename, 'r+')
    file_list = f.read()
    f.close()
    if not os.path.exists(seg_path):
        os.mkdir(seg_path)
    seg_list = jieba.cut(file_list, cut_all=True)
    seg_list = [w for w in seg_list if w not in stopwords_set]
    return seg_list


if __name__ == '__main__':
    vectorizer = CountVectorizer()
    vectorizer.fit_transform()

import os
import jieba
import re
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.feature_extraction.text import CountVectorizer
import pandas as pd
from collections import OrderedDict


def avail_tfidf():
    path = './tfidffile/'
    file_list = [f for f in os.listdir(path) if f != '.' and f != '.DS_S.txt' and f != '.DS_Store']
    _map = {}
    _total = len(file_list)
    cnt = 0
    for f in file_list:
        cnt += 1
        print('\r%d/%d' % (cnt, _total), end='')
        with open(path + f) as f:
            _map_item = {}
            for line in f.readlines():
                line = line.strip().replace('\n', '')
                if line == '':
                    continue
                # print(line)
                _word, _tfidf = line.split(',')
                _tfidf = float(_tfidf)
                if _tfidf == 0:
                    continue
                if _word in _map_item:
                    _map_item[_word] += _tfidf
                else:
                    _map_item[_word] = _tfidf
            d = OrderedDict(_map_item)
            bar = OrderedDict(sorted(d.items(), key=lambda x: x[1]))
            print(bar)
            print(list(bar.items()))
            exit(0)

    df = pd.DataFrame([[k, v] for k, v in _map.items()], columns=['word', 'tfidf'])
    df = df.sort_values(by='tfidf', ascending=False)
    df.to_csv('avail_tfidf.csv')


if __name__ == "__main__":
    # allfile, path = getFilelist('/Users/baidu/tmp/zrbg_txt_2010/')
    # for ff in allfile:
    #     print("Using jieba on " + ff)
    #     fenci(ff, path)
    #
    # tf_idf()
    avail_tfidf()

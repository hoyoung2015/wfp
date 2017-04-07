import os
import jieba
import re
from sklearn.feature_extraction.text import TfidfTransformer
from sklearn.feature_extraction.text import CountVectorizer
import pandas as pd


# 获取文件列表（该目录下放着100份文档）
def getFilelist(path):
    filelist = []
    files = os.listdir(path)
    for f in files:
        if f == '.' or f == '.DS_S.txt' or f == '.DS_Store':
            pass
        else:
            filelist.append(f)
    return filelist, path


# 对文档进行分词处理
def fenci(argv, path):
    # 保存分词结果的目录
    sFilePath = './segfile'
    if not os.path.exists(sFilePath):
        os.mkdir(sFilePath)
    # 读取文档
    filename = argv
    f = open(path + filename, 'r+')
    file_list = f.read()
    f.close()

    # 对文档进行分词处理，采用默认模式
    seg_list = jieba.cut(file_list, cut_all=True)

    # 对空格，换行符进行处理
    result = []
    for seg in seg_list:
        seg = ''.join(seg.split())
        if re.match('[\d#\+\-\*]+', seg) or len(seg) == 1:
            continue
        if re.match('[a-zA-Z]', seg):
            continue
        if seg != '' and seg != "\n" and seg != "\n\n":
            result.append(seg)
    # 将分词后的结果用空格隔开，保存至本地。比如"我来到北京清华大学"，分词结果写入为："我 来到 北京 清华大学"
    f = open(sFilePath + "/" + filename + "-seg.txt", "w+")
    f.write(' '.join(result))
    f.close()  # 读取100份已分词好的文档，进行TF-IDF计算


def tf_idf():
    path = './segfile/'
    file_list = [f for f in os.listdir(path) if f != '.' and f != '.DS_S.txt' and f != '.DS_Store']
    corpus = []  # 存取100份文档的分词结果
    for ff in file_list:
        fname = path + ff
        f = open(fname, 'r+')
        content = f.read()
        f.close()
        corpus.append(content)

    vectorizer = CountVectorizer(analyzer=lambda s: s.split())
    transformer = TfidfTransformer()
    tfidf = transformer.fit_transform(vectorizer.fit_transform(corpus))

    word = vectorizer.get_feature_names()  # 所有文本的关键字
    # for x in word:
    #     print(x)

    weight = tfidf.toarray()  # 对应的tfidf矩阵

    sFilePath = './tfidffile'
    if not os.path.exists(sFilePath):
        os.mkdir(sFilePath)

    # 这里将每份文档词语的TF-IDF写入tfidffile文件夹中保存
    for i in range(len(weight)):
        print("--------Writing all the tf-idf in the", i, " file into ",
              sFilePath + '/' + str(i).zfill(5) + '.txt', "--------")
        f = open(sFilePath + '/' + str(i).zfill(5) + '.txt', 'w+')
        for j in range(len(word)):
            f.write(word[j] + ',' + str(weight[i][j]) + "\n")
        f.close()


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
            for line in f.readlines():
                line = line.strip().replace('\n', '')
                if line == '':
                    continue
                # print(line)
                _word, _tfidf = line.split(',')
                _tfidf = float(_tfidf)
                if _tfidf == 0:
                    continue
                if _word in _map:
                    _map[_word] += _tfidf
                else:
                    _map[_word] = _tfidf
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

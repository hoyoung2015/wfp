import pandas as pd
from nltk.collocations import BigramCollocationFinder
from nltk.corpus import stopwords
import jieba

with open('/Users/baidu/workspace/wfp/wfp-python/data/doc_sample.txt') as f:
    doc = f.read()

print(doc)

stopwords_set = stopwords.words('chinese')
l = [w for w in jieba.cut(doc, cut_all=True) if w not in stopwords_set]
bigram_finder = BigramCollocationFinder.from_words(l)
bigram_finder.apply_word_filter(lambda x: x in set(['']))
print(l)
for k, v in bigram_finder.ngram_fd.items():
    print(k, v)

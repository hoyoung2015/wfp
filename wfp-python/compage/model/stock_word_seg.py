from nltk.collocations import BigramCollocationFinder, BigramAssocMeasures, TrigramCollocationFinder
import nltk
import jieba
from common.mongo import mongo_cli
import re

stopwords_set = set(nltk.corpus.stopwords.words('chinese'))
stopwords_set.add('')
stopwords_set.add(' ')
stopwords_set.add('\xa0')
stopwords_set.add('\u3000')


def read_words_from_mongo(stock_code=''):
    _rs = []
    _collection = mongo_cli.get_database('wfp_com_page').get_collection(stock_code)
    _total = _collection.count()
    _cnt = 0
    for d in _collection.find({}, projection={'content': 1}):
        _cnt += 1
        print('\r%d/%d' % (_cnt, _total), end='')
        words = jieba.cut(d['content'], cut_all=False)
        for word in words:
            if word in stopwords_set:
                continue
            _rs.append(word)
    return _rs


stocks = [
    x for x in mongo_cli.get_database('wfp_com_page').collection_names(include_system_collections=False) if re.match(
        '^\d{6}$', x)]
cnt = 0
stock_words_collection = mongo_cli.get_database('wfp').get_collection('stock_words')
for stock in stocks:
    cnt += 1
    db = mongo_cli.get_database('wfp_com_page')
    stock_words = read_words_from_mongo(stock)
    stock_words_collection.update_one({'stockCode': stock},
                                      {'$set': {
                                          'word_seg': stock_words,
                                          'words_num': len(stock_words),
                                          'total_page': db.get_collection(stock).count()}
                                      },
                                      upsert=True)
    print('\t%d\t%s\t%d' % (cnt, stock, len(stock_words)))
    break

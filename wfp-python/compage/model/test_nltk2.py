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


def read_keywords(file=''):
    _rs_set = set()
    with open(file) as f:
        for line in f.readlines():
            line = line.strip().replace('\n', '')
            if line == '':
                continue
            _rs_set.add(line.split('\t')[0])
    return _rs_set


keywords = read_keywords('/Users/baidu/workspace/wfp/wfp-python/zrbg/pdfminer/word_seg_result_top1.txt')


def read_words_from_mongo(stock_code=''):
    _collection = mongo_cli.get_database('wfp_com_page').get_collection(stock_code)
    _total = _collection.count()
    _cnt = 0
    _rs_map = {}
    _green_total = 0
    for d in _collection.find({}, projection={'content': 1}):
        _cnt += 1
        print('\r%d/%d' % (_cnt, _total), end='')
        words = jieba.cut(d['content'], cut_all=True)
        bigram_finder = BigramCollocationFinder.from_words([w for w in words if w not in stopwords_set])
        bigram_finder.apply_word_filter(lambda x: x not in keywords)
        bigram_finder.apply_freq_filter(1)
        if len(bigram_finder.ngram_fd.values()) > 0:
            _green_total += 1
        for w, n in bigram_finder.ngram_fd.items():
            if w[0] == w[1]:
                continue
            if w[0] in w[1] or w[1] in w[0]:
                continue
            if w in _rs_map:
                _rs_map[w] += n
            else:
                _rs_map[w] = n
    return _rs_map, _green_total, _total


stocks = [
    x for x in mongo_cli.get_database('wfp_com_page').collection_names(include_system_collections=False) if re.match(
        '^\d{6}$', x)]
cnt = 0
bigram_words_collection = mongo_cli.get_database('wfp').get_collection('bigram_words')
for stock in stocks:
    cnt += 1
    rs_map, green_total, total = read_words_from_mongo(stock)
    green_ngram_words_num = sum(rs_map.values())
    bigram_words_collection.update_one({
        'stockCode': stock
    }, {
        '$set': {
            'green_total': green_total,
            'total': total,
            'green_ngram_words_num': green_ngram_words_num,
            'ngram_words': sorted([[k[0], k[1], v] for k, v in rs_map.items()], key=lambda x: x[2], reverse=True)
        }
    }, upsert=True)
    print('\t%d\t%s\t%d\t%d\t%d' % (cnt, stock, green_total, total, green_ngram_words_num))
    # break

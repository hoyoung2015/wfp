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
    _rs = []
    _collection = mongo_cli.get_database('wfp_com_page').get_collection(stock_code)
    _total = _collection.count()
    _cnt = 0
    _rs_map = {}
    for d in _collection.find({}, projection={'content': 1}):
        _cnt += 1
        print('\r%d/%d' % (_cnt, _total), end='')
        words = jieba.cut(d['content'], cut_all=False)
        bigram_finder = BigramCollocationFinder.from_words([w for w in words if w not in stopwords_set])
        bigram_finder.apply_word_filter(lambda x: x not in keywords)
        for x in bigram_finder.ngram_fd:
            print(x)

    print('-' * 200)


# print(len(read_words_from_mongo('000012')))

# print(stopwords.words('chinese'))
# words = nltk.corpus.genesis.words('english-web.txt')
# print(len(words))
# exit(0)

# a = BigramCollocationFinder()
# a.nbest(BigramAssocMeasures.likelihood_ratio, 10)

# read_words_from_mongo('000153')
# ['为', '社会主义', '做贡献', '节能', '减排', '是', '我们', '目标', '节能', '和', '减排', '节能', '减排']


stocks = [
    x for x in mongo_cli.get_database('wfp_com_page').collection_names(include_system_collections=False) if re.match(
        '^\d{6}$', x)]
cnt = 0
bigram_collection = mongo_cli.get_database('wfp').get_collection('bigram')

freq_filter = 1

for stock in stocks:
    cnt += 1
    # if bigram_collection.count({'$and': [
    #     {'stockCode': stock},
    #     {'words_num' + str(freq_filter): {'$exists': True}}
    # ]}) > 0:
    #     continue
    read_words_from_mongo(stock)
    print()

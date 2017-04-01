import jieba
import re
from common.mongo import mongo_cli
from nltk.corpus import stopwords


def get_collection_names(db_name):
    db = mongo_cli.get_database(db_name)
    return [name for name in db.collection_names() if re.match('^\d{6}$', name)]


db_name = 'wfp_com_page'

stopwords_set = set(stopwords.words('chinese'))
stopwords_set.add('')
stopwords_set.add(' ')
stopwords_set.add('\xa0')
stopwords_set.add('\u3000')

stocks = get_collection_names(db_name)
i = 0
for stock in stocks:
    i += 1
    collection = mongo_cli.get_database(db_name).get_collection(stock)
    j = 0
    filters = {
        'words': {
            '$exists': False
        }
    }
    total = collection.count(filters)
    for d in collection.find(filters, projection={'content': 1}):
        j += 1
        content = d['content']
        words_map = {}
        print('\r%d\t%s\t%d/%d' % (i, stock, j, total), end='')
        for w in jieba.cut(content, cut_all=True):
            if w in stopwords_set or len(w) < 2:
                continue
            if w in words_map:
                words_map[w] += 1
            else:
                words_map[w] = 1

        collection.update_one({
            '_id': d['_id']
        }, {
            '$set': {
                'words': words_map
            }
        })
    print()

from common.mongo import mongo_cli
import re
import jieba


def get_collection_names(db_name):
    db = mongo_cli.get_database(db_name)
    return [name for name in db.collection_names() if re.match('^\d{6}$', name)]


def read_stopwords(filename):
    words = set()
    with open(filename) as f:
        for s in f.readlines():
            s = s.strip().replace('\n', '')
            words.add(s)
        f.close()
    return words


if __name__ == '__main__':
    dbname = 'wfp_com_page_test'
    db = mongo_cli.get_database(dbname)
    stopwords = read_stopwords('../data/stopwords.txt')
    for collection_name in get_collection_names(dbname):
        collection = db.get_collection(collection_name)
        filter = {
            'content': {
                '$exists': True
            }
        }
        count = collection.count(filter)
        for doc in collection.find(filter, projection=['content']):
            content = doc['content']
            # print(content)
            words_list = jieba.cut(content, cut_all=True, HMM=True)
            wordmap = {}
            for word in words_list:
                if word in stopwords:
                    continue
                if word in wordmap:
                    wordmap[word] += + 1
                else:
                    wordmap[word] = 1
            print(wordmap)
            if len(wordmap) == 0:
                collection.delete_one({'_id': doc['_id']})
                # break
        break

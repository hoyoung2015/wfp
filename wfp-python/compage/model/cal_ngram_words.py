from common.mongo import mongo_cli

bigram_words_collection = mongo_cli.get_database('wfp').get_collection('bigram_words')

m = {}
for x in bigram_words_collection.find():
    words = x['ngram_words']
    for w in words:
        k = (w[0], w[1])
        if k in m:
            m[k] += w[2]
        else:
            m[k] = w[2]

l = [[k[0], k[1], v] for k, v in m.items()]
l = sorted(l, key=lambda x: x[2], reverse=True)
print(len(l))
for x in l:
    print(x)

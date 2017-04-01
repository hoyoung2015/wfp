from common.mongo import mongo_cli
import re
import pandas as pd

"""
统计原始有多少页面
"""
db_name = 'wfp_com_page'
db = mongo_cli.get_database(db_name)
stocks = [stock for stock in db.collection_names(include_system_collections=False) if re.match('\d+', stock)]

data_array = []

cnt = 0
for stock in stocks:
    cnt += 1
    collection = db.get_collection(stock)
    page_size = collection.count()
    doc_size = collection.count({
        '$or': [{'url': {
            '$regex': '\.(pdf|PDF|doc|DOC|docx|DOCX)$'
        }}, {'contentType': {
            '$in': ['pdf', 'msword']
        }}]
    })
    html_size = page_size - doc_size
    data_array.append([stock, page_size, doc_size, html_size])
    print('%d\t%s\t%d\t%d\t%d' % (cnt, stock, page_size, doc_size, html_size))

df = pd.DataFrame(data_array, columns=['stockCode', 'pageSize', 'docSize', 'htmlSize'])
print(df.head())
df.to_csv('wfp_com_page_desc.csv', index=False)

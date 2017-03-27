import pandas as pd
import numpy as np
from common.mongo import mongo_cli

total_avail_df = pd.read_csv('total_avail.txt', sep='\t', dtype={'stockCode': np.str})

filters = {
    '$or': [{'url': {
        '$regex': '\.(pdf|PDF|doc|DOC|docx|DOCX)$'
    }}, {'contentType': {
        '$in': ['pdf', 'msword']
    }}]
}

db = mongo_cli.get_database('wfp_com_page')
cnt = 0
for row in total_avail_df.values:
    cnt += 1
    stock_code = row[0]
    if row[3] > 0:
        rs = db.get_collection(stock_code).delete_many(filters)
    print('%d-%s' % (cnt, stock_code))

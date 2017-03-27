import pandas
import numpy as np
from common.mongo import mongo_cli

total_df = pandas.read_csv('total.txt', sep='\t', dtype={'stockCode': np.str})
total_doc_df = pandas.read_csv('total_doc.txt', sep='\t', dtype={'stockCode': np.str})

df = total_df.join(total_doc_df.set_index('stockCode'), on='stockCode')
df['docPercent'] = df['totalDoc'] / df['total']

df = df.sort_values(by='docPercent', ascending=False)

df = df.loc[df['docPercent'] > 0.2]
print(df.shape)
db = mongo_cli.get_database('wfp_com_page')
for row in df.values:
    db.drop_collection(row[0])
# df.to_csv('total_avail.txt', sep='\t',index=None)

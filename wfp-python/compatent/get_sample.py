from common.mongo import mongo_cli
import pandas as pd

data_array = []
for d in mongo_cli.get_database('wfp_com_patent').get_collection('002050').find():
    data_array.append([d['patentName'], d['patentType'], d['mainClassId'], d['date']])

df = pd.DataFrame(data_array, columns=['专利名称', '专利类型', '分类号', '公开日'])
n = 20
s = 30
df[s:s+20].to_excel('sample.xlsx', index=False)

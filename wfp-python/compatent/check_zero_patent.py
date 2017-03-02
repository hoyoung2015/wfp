from pymongo import MongoClient

"""
对于专利为0的企业，再检查一次
"""
client = MongoClient(host='10.170.47.245', port=27017)
db = client.get_database('wfp_com_patent')
desc = db.get_collection('description')

fuck = [x['stockCode'] for x in desc.find() if x['total'] == 0]

com = {}
file = '../../wfp-patent/src/main/shell/bin/com_info.txt'

with open(file) as f:
    for line in f.readlines():
        line = line.strip().replace('\n', '')
        if line=='' or line.startswith('#'):
            continue
        com[line.split(',')[0]] = line.split(',')[1]
    f.close()

[print(com[x]) for x in fuck]

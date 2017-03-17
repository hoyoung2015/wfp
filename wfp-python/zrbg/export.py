from common.mongo import mongo_cli

db = mongo_cli.get_database('wfp')

com_code_list = [c['stockCode'] for c in
                 db.get_collection('company_info').find({'is_target': 1}, projection={'stockCode': 1})]

col = db.get_collection('zrbg_info')

query = {
    'year': '2010-12-31',
    'stockCode': {
        '$in': com_code_list
    },
    'Hstock': {
        '$regex': '.+'
    }
}

total = col.count(query)

print(total)
# exit(0)

with open('pdf_url.txt', 'w') as f:
    for c in col.find(query):
        f.write(c['stockCode'] + ',' + c['Hstock'] + '\n')
    f.close()

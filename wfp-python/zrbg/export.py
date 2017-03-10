from common.mongo import mongo_cli

col = mongo_cli.get_database('wfp').get_collection('zrbg_info')

total = col.count({
    'Hstock': {
        '$regex': '.+'
    }
})

print(total)

with open('pdf_url.txt', 'w') as f:
    for c in col.find({
        'Hstock': {
            '$regex': '.+'
        }}):
        f.write(c['stockCode'] + ',' + c['Hstock'] + '\n')
    f.close()

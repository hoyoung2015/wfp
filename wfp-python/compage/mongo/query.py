from common.mongo import mongo_cli

collection = mongo_cli.get_database('wfp_com_page').get_collection('002496')

filter = {
    'url': 'http://www.tech-long.com/show_content.php?cid=91&id=104'
}
print(collection.count())
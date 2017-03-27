import re
from common.mongo import mongo_cli
from common.url_utils import get_domain


def read_regex(file=''):
    _regex_map = {}
    _cur_domain = None
    with open(file) as f:
        for line in f.readlines():
            line = line.strip().replace('\n', '')
            if line == '' or line.startswith('#') or line.startswith('+'):
                continue
            if re.match('^\[([0-9a-zA-Z_\-\.]+)\]$', line):
                _cur_domain = line[1:-1]
                continue
            if _cur_domain in _regex_map:
                _regex_map[_cur_domain] += '|' + line
            else:
                _regex_map[_cur_domain] = line
    return _regex_map


if __name__ == '__main__':

    regex_map = read_regex('../../../wfp-spider/src/main/resources/domain_url_black_list.txt')
    web_source_collection = mongo_cli.get_database('wfp').get_collection('web_source')
    for stock_code in mongo_cli.get_database('wfp_com_page').collection_names(include_system_collections=False):
        stock_code = '600979'

        if re.match('^\d{6}$', stock_code) is False:
            continue
        doc = web_source_collection.find_one({'stockCode': stock_code})
        if doc is None:
            print('%s not exists in web_source' % stock_code)
            continue
        web_site = doc['webSite']
        domain = get_domain(web_site)
        if domain.startswith('www.'):
            domain = domain[4:]

        if domain not in regex_map:
            print('%s %s no regex' % (stock_code, domain))
            continue

        regex = regex_map[domain]
        com_page_collection = mongo_cli.get_database('wfp_com_page').get_collection(stock_code)
        del_list = []
        # filters = {'contentType': {'$exists': True, '$regex': '(html|HTML)'}}
        filters = {}
        total = com_page_collection.count(filters)
        cnt = 0
        for d in com_page_collection.find(filters, projection={'url': 1}):
            cnt += 1
            url = d['url']
            if re.match('(' + regex + ')', url):
                # print(url)
                del_list.append(d['_id'])
            if cnt % 100 == 0:
                print('\r%s\t%d/%d' % (stock_code, cnt, total), end='')
        print()
        if len(del_list) > 0:
            print('will del %d' % len(del_list))
            com_page_collection.delete_many({'_id': {'$in': del_list}})
        break
    mongo_cli.close()

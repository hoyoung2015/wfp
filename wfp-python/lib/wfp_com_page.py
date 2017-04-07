from common.mongo import mongo_cli
import re

db_name = 'wfp_com_page'


def get_stocks(regex='^\d{6}$'):
    stocks = [x for x in mongo_cli.get_database(db_name).collection_names(include_system_collections=False) if
              re.match(regex, x)]
    return stocks


if __name__ == '__main__':
    print(get_stocks())

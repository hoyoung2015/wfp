import pandas as pd
from common.mongo import mongo_cli


def get_all_info():
    index_array = []
    data_array = []
    for d in mongo_cli.get_database('wfp').get_collection('company_info').find({'is_target': 1}):
        stock_code = d['stockCode']
        index_array.append(stock_code)
        data_array.append({
            'stockCode': stock_code,
            'name': d['name'],
            'area': d['area'],
            'industry': d['industry']
        })
    return pd.DataFrame(data_array, index=index_array)


if __name__ == '__main__':
    print(get_all_info().head())

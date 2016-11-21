# -*- coding: utf-8 -*-
"""
导出污染行业的目标企业
"""
__author__ = 'huyang'
from common.mongo import mongo_cli
import pandas as pd
if __name__=='__main__':
    wfp = mongo_cli.get_database('wfp')
    company_info = wfp.get_collection('company_info')
    com_list = [dict(i) for i in company_info.find({'is_target':1},{'_id':0})]
    for com in com_list:
        com.pop('_class')
    df = pd.DataFrame(com_list)
    print(df.head())
    df.to_csv('company_info.csv',header=True,encoding='utf-8',index=False)
    print('success')



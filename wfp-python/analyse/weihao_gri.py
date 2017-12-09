import pandas as pd
from tabulate import tabulate
import numpy as np
from lib.company_info import get_all_info


def pretty_print(df):
    print(tabulate(df, headers='keys', tablefmt='psql'))


df_gri = pd.read_excel('weihao_gri.xlsx')

df_gri = df_gri[df_gri['总分'] > 0]

print(df_gri.shape)

df_gri['stockCode'] = df_gri['证券代码'].apply(lambda x: x[0:6])

# pretty_print(df_gri.head())
# 取重污染行业
all_com = get_all_info()
all_com = all_com[['stockCode', 'industry']].set_index('stockCode')
df_gri = df_gri.join(all_com, on='stockCode', how='inner')


df_gri[['stockCode']].to_csv('stock_gri.csv', index=False, header=False)

exit(0)

df_site = pd.read_excel('web_green_data.xlsx', converters={'stockCode': str})

df_site = df_site.set_index('stockCode')

print(df_site.shape)

df = df_site.join(df_gri.set_index('stockCode'))

df = df.loc[df['总分'] > 0]
df.to_excel('web_green_data_gri.xlsx')

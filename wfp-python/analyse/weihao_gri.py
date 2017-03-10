import pandas as pd
from tabulate import tabulate
import numpy as np


def pretty_print(df):
    print(tabulate(df, headers='keys', tablefmt='psql'))


df_gri = pd.read_excel('weihao_gri.xlsx')

df_gri = df_gri[df_gri['总分'] > 0]

print(df_gri.shape)

df_gri['stockCode'] = df_gri['证券代码'].apply(lambda x: x[0:6])

# pretty_print(df_gri.head())

df_site = pd.read_csv('../compage/web_source.txt', sep='\t', names=['stockCode', 'sname', 'webSite', 'sleepTime'],
                      dtype={'stockCode': np.str})

print(df_site.shape)

df = df_site.join(df_gri.set_index('stockCode'), on='stockCode')

df = df.loc[df['总分'] > 0, ['stockCode', 'sname', '总分']]
print(df.shape)

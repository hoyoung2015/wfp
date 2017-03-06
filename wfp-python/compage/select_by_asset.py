import pandas as pd
import numpy as np


def read_new_industry(filename):
    rs = {}
    with open(filename) as f:
        for line in f.readlines():
            line = line.strip().replace('\n', '')
            if line.startswith('#') or line == '':
                continue
            i = line.index(':')
            rs[line[:i]] = set(line[i + 1:].split(','))
        f.close()
    return rs


com = pd.read_csv('../data/company_info.csv', dtype={'stockCode': np.str})

df = pd.read_csv('web_source.txt', sep='\t', names=['stockCode', 'sname', 'webSite', 'sleepTime'],
                 dtype={'stockCode': np.str})

df = df.join(com.set_index('stockCode'), on='stockCode', rsuffix='_com')

print(df.columns)
# print(df['industry'])
new_ind_map = read_new_industry('new_industry.txt')
for new_ind_key, new_ind_set in new_ind_map.items():
    for new_ind in new_ind_set:
        df.loc[df['industry'] == new_ind, 'industry'] = new_ind_key

df = df.sort_values(by='shareholders', ascending=False)
grouped = df.groupby(by=['industry'])
print(grouped.groups)

min_shareholders = 60

for new_ind, index in grouped.groups.items():
    tmp_df = df.loc[index]
    columns = ['stockCode', 'sname', 'webSite', 'sleepTime']
    tmp_df.loc[tmp_df['shareholders'] >= min_shareholders, columns].to_csv('web_source_crawl.txt', sep='\t', mode='a',
                                                                           header=False,
                                                                           index=False)

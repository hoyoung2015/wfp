from common.mongo import mongo_cli
import re
import os
import pandas as pd

stocks = [
    x for x in mongo_cli.get_database('wfp_com_page').collection_names(include_system_collections=False) if re.match(
        '^\d{6}$', x)]

root_path = '/Users/baidu/tmp/common_words/'
#
# df = pd.DataFrame([[1, 2, 3], [0, 0, 0], [1, 1, 1]], index=['a', 'b', 'c'], columns=['a', 'b', 'c'])
#
# print(df)
# print(df.sum().sum())
# exit(0)

exclude_words = ['能力', '工作']

data = []
i = 0
for stock in stocks:
    i += 1
    file_path = root_path + stock + '.xlsx'
    if os.path.exists(file_path) is False:
        continue
    df = pd.read_excel(file_path, sheetname='Sheet1')
    for r in exclude_words:
        if r in df.index:
            df = df.drop(r, 0)
            df = df.drop(r, 1)
    a = df.sum().sum()
    data.append([stock, a])
    print('%d\t%s\t%d' % (i, stock, a))
df = pd.DataFrame(data, columns=['stockCode', 'double_words_num'])

df.to_excel('double_words.xlsx', index=False, sheet_name='Sheet1')

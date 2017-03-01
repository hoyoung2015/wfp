import pandas as pd
import numpy as np

df = pd.read_csv('../data/company_info.csv', dtype={'stockCode': np.str})

print(df.columns)

data = []

for index, row in df.iterrows():
    data.append(row['stockCode'] + ',' + row['name'] + '\n')

with open('com_info.txt', 'w') as f:
    f.writelines(data)
    f.close()

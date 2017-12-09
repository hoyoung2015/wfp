import pandas as pd
"""
对聚类结果计算五个方面的均值
"""
df = pd.read_csv('clustor_result_data.csv')

df = df.drop('page_size', axis=1)
df = df.drop('stockCode', axis=1)
df = df.drop('total_words_num', axis=1)

k = 5
data_array = []
for i in range(1, k + 1):
    tmp = df.loc[df['result'] == i]
    data_array.append(tmp.mean())

# print(pd.DataFrame(data_array))

mean_data = pd.DataFrame(data_array)

mean_data = mean_data.drop('result', axis=1)

mean_data['sum'] = mean_data.sum(axis=1)
mean_data = mean_data.sort_values(by='sum')
print(mean_data.head())
mean_data.to_csv('clustor_mean_data.csv', index=False)

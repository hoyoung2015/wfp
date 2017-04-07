import pandas as pd

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

pd.DataFrame(data_array).to_csv('clustor_mean_data.csv', index=False)

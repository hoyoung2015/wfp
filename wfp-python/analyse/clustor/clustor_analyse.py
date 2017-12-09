import pandas as pd
from sklearn.cluster import KMeans
from lib.company_info import get_all_info

df = pd.read_excel('clustor_data.xlsx', converters={'stockCode': str})

kmeans_model = KMeans(n_clusters=5, random_state=1)

senator_distances = kmeans_model.fit_predict(df[list(map(lambda x: 'x' + str(x), range(1, 6)))])

df['label'] = kmeans_model.labels_
df = df.set_index('stockCode')

# 读取基本信息
df2 = get_all_info()

df = df.join(df2)

df = df.sort_values(by='label')

df.to_excel('kmeans_clustor_result.xlsx')

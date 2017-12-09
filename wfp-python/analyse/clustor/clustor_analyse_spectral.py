import pandas as pd
from sklearn.cluster import SpectralClustering
from sklearn import metrics
from lib.company_info import get_all_info

df = pd.read_excel('clustor_data.xlsx', converters={'stockCode': str})

clustor = SpectralClustering()
X = df[list(map(lambda x: 'x' + str(x), range(1, 6)))]
y_pred = clustor.fit(X)
print("Calinski-Harabasz Score", metrics.score(X, y_pred))


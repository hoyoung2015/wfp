import pandas as pd
import numpy as np
from sklearn import preprocessing
import seaborn as sns
from sklearn.linear_model import LinearRegression


def a1(datain, columns=[], threshold=.95):
    for col in columns:
        d = datain[col]
        datain = datain.loc[d <= d.quantile(threshold)]
    return datain


def a2(datain, columns=[], threshold=3.5):
    for col in columns:
        d = datain[col]
        zscore = (d - d.mean()) / d.std()
        datain = datain.loc[zscore.abs() <= threshold]
    return datain


def a3(datain, columns=[], threshold=3):
    for col in columns:
        d = datain[col]
        MAD = (d - d.median()).abs().median()
        zscore = ((d - d.median()) * 0.6475 / MAD).abs()
        datain = datain.loc[zscore.abs() <= threshold]
    return datain


data = pd.read_excel('web_green_data.xlsx')
print(data.shape)
# data = a1(data, columns=['score'])

print(data.shape)

#
# print(df.columns)


feature_cols = ['c1'] + ['x1', 'x2', 'x3', 'assets', 'rank','green_patent_num', 'outlink_az', 'indexing_www', 'shouyi', 'news_num']

X = data[feature_cols]

# print(X.head())

y = data['score']

linreg = LinearRegression()

linreg.fit(X, y)

print(linreg.intercept_)
print(linreg.coef_)
print(linreg.singular_)
print(linreg)
print('-' * 100)
print(feature_cols)
# print(preprocessing.normalize(linreg.coef_.tolist(), norm='l1'))

print(linreg.score(X, y))

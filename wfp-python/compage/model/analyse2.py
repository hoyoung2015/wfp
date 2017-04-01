import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

df = pd.read_excel('web_green_data.xlsx', sheetname='Sheet1', converters={'stockCode': str})
df = df.set_index('stockCode')
fig, ax = plt.subplots()

y = df['score']
x = df['x8']
labels = df.index
ax.scatter(x, y)

for i, txt in enumerate(df.index):
    ax.annotate(txt, (x[i], y[i]))

fig = plt.gcf()
fig.set_size_inches(14, 10)

fig.savefig('analyse.png', dpi=100)
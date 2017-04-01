import pandas as pd
import seaborn as sns

df = pd.read_excel('web_green_data.xlsx', sheetname='Sheet1')

# list(map(lambda x: 'x' + str(x), range(1, 9)))

sns.pairplot(df, x_vars=['x7'], y_vars='score', size=5, aspect=0.8)
sns.plt.show()

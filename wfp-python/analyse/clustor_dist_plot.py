import seaborn as sns
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

df = pd.read_excel('clustor_data.xlsx')
sns.distplot(df['x5'])
plt.show()

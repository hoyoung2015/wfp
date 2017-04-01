import seaborn as sns
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

sns.set(color_codes=True)

df = pd.read_excel('web_green_data.xlsx')

sns.distplot(df['indexing_www'])
plt.show()

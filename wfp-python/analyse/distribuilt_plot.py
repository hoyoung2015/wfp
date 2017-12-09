import seaborn as sns
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
"""
已经接近正太分布的变量
x1
x2
x3
assets
rank
outlink_az
indexing_www 勉强
shouyi 没有做任何处理
news_num 勉强
green_patent_num
score
"""
sns.set(color_codes=True)

df = pd.read_excel('web_green_data_gri.xlsx')

sns.distplot(df['总分'])
plt.show()

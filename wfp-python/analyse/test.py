import pandas as pd

df = pd.DataFrame({
    'a': [1, 2, 3, 4, 3, 2],
    'b': [1, 2, 3, 4, 4, 5]
})
group_a = df.groupby('a')
print(group_a.size())

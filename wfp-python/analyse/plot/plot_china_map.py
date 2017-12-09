import pandas as pd
from lib.company_info import get_all_info
from sklearn.preprocessing import MinMaxScaler

df = pd.read_excel('../web_green_data_bak.xlsx', converters={'stockCode': str})
df = df.set_index('stockCode')
com_all = get_all_info()
df = df.join(com_all)[['EDI', 'area']]
df = df.groupby('area')
print(df.mean())
se = df.mean()

min_max_scaler = MinMaxScaler()
min_x = se.min()['EDI']
max_x = se.max()['EDI']
print(min_x)
print(max_x)

se = se.apply(lambda x: (x - min_x) / (max_x - min_x))

print(se)
# print(min_max_scaler.fit_transform(se))

data = []

for name in se.index:
    data.append({
        'name': name,
        'value': float('%.3f' % se.ix[name]['EDI'])
    })
print(data)

a = [{'value': 0.306, 'name': '上海'},
     {'value': 0.987, 'name': '云南'},
     {'value': 0.641, 'name': '内蒙古'},
     {'value': 0.536, 'name': '北京'},
     {'value': 0.668, 'name': '吉林'},
     {'value': 0.338, 'name': '四川'},
     {'value': 1.0, 'name': '天津'},
     {'value': 0.471, 'name': '宁夏'},
     {'value': 0.556, 'name': '安徽'},
     {'value': 0.384, 'name': '山东'},
     {'value': 0.378, 'name': '山西'},
     {'value': 0.308, 'name': '广东'},
     {'value': 0.275, 'name': '广西'},
     {'value': 0.28, 'name': '新疆'},
     {'value': 0.473, 'name': '江苏'},
     {'value': 0.534, 'name': '江西'},
     {'value': 0.548, 'name': '河北'},
     {'value': 0.456, 'name': '河南'},
     {'value': 0.564, 'name': '浙江'},
     {'value': 0.0, 'name': '海南'},
     {'value': 0.436, 'name': '湖北'},
     {'value': 0.291, 'name': '湖南'},
     {'value': 0.786, 'name': '甘肃'},
     {'value': 0.748, 'name': '福建'},
     {'value': 0.328, 'name': '西藏'},
     {'value': 0.754, 'name': '贵州'},
     {'value': 0.428, 'name': '辽宁'},
     {'value': 0.519, 'name': '重庆'},
     {'value': 0.544, 'name': '陕西'},
     {'value': 0.696, 'name': '青海'},
     {'value': 0.329, 'name': '黑龙江'}
     ]

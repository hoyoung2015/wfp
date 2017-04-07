import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

com = pd.read_csv('../data/company_info.csv', dtype={'stockCode': np.str})
com = com.set_index('stockCode')

sample = pd.read_csv('clustor_data.csv', dtype={'stockCode': np.str})
sample = sample.set_index('stockCode')
sample = sample.join(com[['area', 'sname']])
# print(sample.head())
# print(com)

area_map = {
    '东南': ['上海', '广东', '江苏', '江西', '浙江', '海南', '湖南', '福建'],
    '西南': ['云南', '四川', '广西', '西藏', '贵州', '重庆'],
    '华中': ['云南', '北京', '天津', '安徽', '山东', '山西', '河北', '河南', '湖北'],
    '东北': ['内蒙古', '吉林', '辽宁', '黑龙江'],
    '西北': ['云南', '宁夏', '新疆', '甘肃', '陕西', '青海']
}


def get_area(province):
    for k, v in area_map.items():
        if province in v:
            return k


group_area = sample.groupby('area')
size = group_area.size()
rs = {}
for province in size.index:
    area = get_area(province)
    if area in rs:
        rs[area] += size[province]
    else:
        rs[area] = size[province]

print(rs)
print(pd.Series(rs))

se = pd.Series(rs)

plt.hist(se)
plt.show()

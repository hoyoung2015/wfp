import pandas as pd
from common.mongo import mongo_cli


def get_all_info():
    index_array = []
    data_array = []
    for d in mongo_cli.get_database('wfp').get_collection('company_info').find({'is_target': 1}):
        stock_code = d['stockCode']
        index_array.append(stock_code)
        data_array.append({
            'stockCode': stock_code,
            'name': d['name'],
            'sname': d['sname'],
            'area': d['area'],
            'industry': d['industry']
        })
    return pd.DataFrame(data_array, index=index_array)


area_map = {
    '东南': ['上海', '广东', '江苏', '江西', '浙江', '海南', '湖南', '福建'],
    '西南': ['云南', '四川', '广西', '西藏', '贵州', '重庆'],
    '华中': ['云南', '北京', '天津', '安徽', '山东', '山西', '河北', '河南', '湖北'],
    '东北': ['内蒙古', '吉林', '辽宁', '黑龙江'],
    '西北': ['云南', '宁夏', '新疆', '甘肃', '陕西', '青海']
}

# industry_map = {
#     '非金属制造业（含医药、化工、橡胶、塑料）': ['制药', '医药与生物科技', '特种化工品', '通用化工品', '造纸', '轮胎'],
#     '机械设备制造业（含通用、专用及运输设备）': ['工业机械', '汽车', '汽车与零配件（II）'],
#     '食品加工制造业（含饮料）': ['食品生产（IV）'],
#     '采掘业': ['普通矿开采', '油气综采', '煤炭开采', '采矿业', '金矿开采', '石油勘探与开采', '采油设备与服务'],
#     '金属制造业（含黑色、有色金属冶炼及压延加工）': ['工业金属', '有色金属', '铝业', '钢铁'],
#     '电子电器制造业（含通讯、计算机）': ['电子电气组件与设备'],
#     '其他（含建材、电力供应）': []
# }
industry_map = {
    '医药与生物科技': ['制药', '医药与生物科技'],
    '工业机械': ['工业机械', '工业金属'],
    '建材与构件': ['建材与构件', '建筑与材料（III）'],
    '采掘业': ['普通矿开采', '油气综采', '煤炭开采', '采矿业', '金矿开采', '石油勘探与开采', '采油设备与服务'],
    '有色金属': ['有色金属'],
    '汽车与零配件': ['汽车', '汽车与零配件（II）', '轮胎'],
    '特种化工品': ['特种化工品'],
    '电力': ['电力（IV）'],
    '电子电气组件与设备': ['电子电气组件与设备'],
    '通用化工品': ['通用化工品'],
    '造纸': ['造纸'],
    '钢铁': ['钢铁'],
    '铝业': ['铝业'],
    '食品生产': ['食品生产（IV）']
}


def get_industry(old_industry):
    for k, v in industry_map.items():
        if old_industry in v:
            return k
    return '其他'


def get_area(province):
    for k, v in area_map.items():
        if province in v:
            return k


if __name__ == '__main__':
    print(get_all_info().head())

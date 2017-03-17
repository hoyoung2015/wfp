def dict_local_type(d={}):
    '''
    将sereis的to_dict方法得到的dict的value类型转为python类型
    :param d:
    :return:
    '''
    rs = {}
    for k, v in d.items():
        try:
            rs[k] = v.item()
        except AttributeError as e:
            rs[k] = v
    return rs

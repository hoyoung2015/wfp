import json

with open('patents.json') as fp:
    m = {}
    for k, v in json.load(fp).items():
        if v in m:
            m[v].append(k)
        else:
            m[v] = [k]
    for k, v in m.items():
        print(k, ','.join(v))

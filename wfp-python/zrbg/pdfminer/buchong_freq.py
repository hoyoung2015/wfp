import pandas as pd

words_set = set()
with open('/Users/baidu/workspace/wfp/wfp-python/zrbg/pdfminer/word_seg_result_top1.txt') as f:
    for line in f.readlines():
        line = line.strip().replace('\n', '')
        if line == '':
            continue
        words_set.add(line.split('\t')[0])

data_list = []
with open('/Users/baidu/workspace/wfp/wfp-python/zrbg/pdfminer/word_seg_result_freq.txt') as f:
    for line in f.readlines():
        line = line.strip().replace('\n', '')
        if line == '':
            continue
        w, n, f = line.split('\t')
        if w not in words_set:
            continue
        data_list.append([w, int(n), float(f)])
df = pd.DataFrame(data_list, columns=['word', 'num', 'freq'])
df.to_csv('final_word.txt', sep='\t', index=False)

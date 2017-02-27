import re
import sys
import os

pattern = re.compile('\d{18}, aboutme=(.+)')


def process(path):
    print('process', path)
    with open(path) as f:
        while True:
            line = f.readline()
            if line is None or line == '':
                break
            m = pattern.match(line)
            if m:
                rs.append(m.groups()[0] + '\n')
        f.close()


rs = []
if len(sys.argv) != 2:
    print('Usage: python tong.py dir')
    exit(-1)
root_dir = sys.argv[1]

for file in os.listdir(root_dir):
    path = os.path.join(root_dir, file)
    if os.path.isfile(path):
        process(path)
with open('output.txt', 'w') as f:
    f.writelines(rs)
    f.close()

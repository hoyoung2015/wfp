def read_lines(file):
    rs = []
    with open(file) as f:
        for line in f.readlines():
            line = line.strip().replace('\n', '')
            if line == '':
                continue
            rs.append(line)
    return rs


if __name__ == '__main__':
    pass
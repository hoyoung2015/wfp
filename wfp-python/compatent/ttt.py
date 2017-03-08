import time

total = 10
for x in range(total):
    n = 0
    print('\r%d/%d' % (++n, total), end='')
    time.sleep(0.4)

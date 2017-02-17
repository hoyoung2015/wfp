import queue
import time
from multiprocessing.pool import ThreadPool

test_queue = queue.Queue()

test_queue.put(1)
test_queue.put(2)

# print(test_queue.qsize())
# print(test_queue.empty())
#
#
# print(test_queue.qsize())

while test_queue.qsize() > 0:
    print(test_queue.get())

n = None
print(n is not None)

la = [1]
lb = [2, 3]
print("test list append")
la[0:0] = lb
[print(x) for x in la]


def test(a):
    time.sleep(1)
    return a + 1


def testb():
    return [1, 2, 3]


pool0 = ThreadPool(2)
rs0 = [pool0.apply_async(testb, args=()) for x in range(2)]
pool0.close()
pool0.join()
la = []
for x in rs0:
    la[0:0] = x.get()
print("pool0", la)

pool = ThreadPool(5)

result = []
[result.append(pool.apply_async(test, args=(x,))) for x in range(5)]

pool.close()
pool.join()

print('hello')
tmp = [x.get() for x in result if x.get() % 2 == 0]
[print(y) for y in tmp]

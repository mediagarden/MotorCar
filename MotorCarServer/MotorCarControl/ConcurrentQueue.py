# encoding='utf-8'
import queue
import threading


class ConcurrentQueue:
    def __init__(self, capacity=-1):
        self.__capacity = capacity  # 初始化队列大小
        self.__mutex = threading.Lock()  # 初始化互斥量
        self.__cond = threading.Condition(self.__mutex)  # 初始化条件变量
        self.__queue = queue.Queue()  # 初始化队列

    def get(self):

        if self.__cond.acquire():  # 获取互斥锁和条件变量，python中threading条件变量默认包含互斥量，因此只需要获取条件变量即可
            while self.__queue.empty():
                self.__cond.wait()  # 条件变量等待
            elem = self.__queue.get()
            self.__cond.notify()
            self.__cond.release()
        return elem

    def put(self, elem):
        if self.__cond.acquire():
            while self.__queue.qsize() >= self.__capacity:
                self.__cond.wait()
            self.__queue.put(elem)
            self.__cond.notify()
            self.__cond.release()

    def clear(self):
        if self.__cond.acquire():
            self.__queue.queue.clear()
            self.__cond.release()
            self.__cond.notifyAll()

    def empty(self):
        is_empty = False;
        if self.__mutex.acquire():  # 只需要获取互斥量
            is_empty = self.__queue.empty()
            self.__mutex.release()
        return is_empty

    def size(self):
        size = 0
        if self.__mutex.acquire():
            size = self.__queue.qsize()
            self.__mutex.release()
        return size

    def resize(self, capacity=-1):
        self.__capacity = capacity

# encoding='utf-8'
from MotorCarControl.ConcurrentQueue import ConcurrentQueue
import threading
import logging
from time import ctime, sleep


class MotorCarControl:
    def __init__(self, logging):
        self.queue = ConcurrentQueue(1000)
        self.logging = logging
        self.tag = "MotorCar Control"
        self.SLEEP_TIME = 0.1

    def run(self):
        self.serverThread = threading.Thread(target=self.__runServer)
        self.serverThread.setDaemon(True)
        self.serverThread.start()

    def __runServer(self):
        while True:
            if (self.queue.empty()):
                sleep(self.SLEEP_TIME)
            else:
                self.__exeControlMessage(self.queue.get())

    def __exeControlMessage(self, msg):
        # 停留self.SLEEP_TIME秒时间
        # TODO:CONTROL
        sleep(self.SLEEP_TIME)
        self.logging.info(self.tag + ": Execute The Command!")
        return

    def putControlMessage(self, msg):
        # TODO:CHECK
        self.logging.info(self.tag + ": Put Message!")
        self.queue.put(msg)
        self.logging.info(self.tag + ": Put Message!")
        return True

# -*- coding:utf-8 -*-
import threading


# 拍照服务函数
# __cameraDemo：模拟拍照数据获取
class CameraDriver:
    cameraLock = threading.Lock()

    def __init__(self, logging):
        self.logging = logging
        self.tag = "Camera Driver"

    def camera(self):
        CameraDriver.cameraLock.acquire()
        self.logging.info(self.tag + ":Camera Begin to Take Photo!")
        # TODO:开始拍照
        buffer = self.__cameraDemo()
        CameraDriver.cameraLock.release()
        return buffer

    def __cameraDemo(self):
        file = open('Doraemon.jpg', 'rb+')
        fileBuffer = bytearray()
        while True:
            buffer = file.read()
            if (len(buffer) != 0):
                fileBuffer += buffer
            else:
                break
        file.close()
        self.logging.info(self.tag + ":Camera Demo Function Return Demo Photo!")
        self.logging.info(self.tag + ":The Photo Size :%s!", str(len(fileBuffer)))
        return fileBuffer

# -*- coding:utf-8 -*-
from CameraServer.CameraDriver import CameraDriver
import threading
import socket
import logging


# 拍照数据服务
# 请求数据：无，连接到端口即开始拍照。
# 回复数据：
# Byte 0	Byte 1	Byte 2	Byte 3						Byte N
# Byte 0：Byte 3：整个数据包（包括Byte 0：Byte 1）的长度（低位优先）。DATA_LEN = Byte 0 + (Byte 1<<8)+(Byte 2<<16)+(Byte 3<<24)。
# Byte 4：Byte N：JPG类型的图片文件。

class CameraServer:
    def __init__(self, config, logging):
        self.logging = logging
        self.config = config
        self.serverSocket = None
        self.serverThread = None
        self.tag = "Camera Server"

    def run(self):
        self.serverThread = threading.Thread(target=self.__runServer)
        self.serverThread.setDaemon(True)
        self.serverThread.start()

    def __runServer(self):
        self.logging.info(self.tag + ": Socket Server Running!")
        self.serverSocket = socket.socket()
        self.serverSocket.bind((self.config["IP"], self.config["Port"]))
        self.serverSocket.listen(5)
        self.clientInfo = []
        while (True):
            conn, address = self.serverSocket.accept()
            client = {"Connection": conn, "Address": address, "Thread": "None"}
            clientThread = threading.Thread(target=self.__clientServer, args=(client,))
            client["Thread"] = clientThread
            clientThread.setDaemon(True)
            clientThread.start()
            self.clientInfo.append(client)

    def __clientServer(self, client):
        # 返回拍照数据
        self.logging.info(self.tag + ": New Client Connection:" + str(client["Address"]))
        clientConnection = client["Connection"]
        try:
            cameraDriver = CameraDriver(logging)
            picBuffer = cameraDriver.camera()
            lenPic = len(picBuffer)
            # DATA_LEN = Byte 0 + (Byte 1<<8)+(Byte 2<<16)+(Byte 3<<24)
            lenBuffer = bytearray()
            lenBuffer.append(lenPic & 0xFF)
            lenBuffer.append((lenPic & 0xFF00) >> 8)
            lenBuffer.append((lenPic & 0xFF0000) >> 16)
            lenBuffer.append((lenPic & 0xFF000000) >> 24)
            clientConnection.send(lenBuffer)
            self.logging.info(self.tag + ": The Photo Size was Sent%s!", str(lenBuffer))
            self.logging.info(self.tag + ": The Photo Size was Sent!")
            self.logging.info(self.tag + ": Begin to Send Data of Photo!")
            sentLen = 0
            while sentLen < lenPic:
                sendLen = clientConnection.send(picBuffer[sentLen:sentLen + 4*1024])
                sentLen += sendLen
                self.logging.info(self.tag + ": Send Data Size: %s/%s!", sendLen, sentLen)
            self.logging.info(self.tag + ": The Photo was Sent!")
        except socket.error as msg:
            self.logging.warning(self.tag + ": Closed Client Connection:" + str(client["Address"]))
        finally:
            clientConnection.close()
            self.clientInfo.remove(client)

# -*- coding:utf-8 -*-
from socket import *


# 模拟APP客户端获取照片
class CameraClient:
    def __init__(self,config,logging):
        client = socket(AF_INET, SOCK_STREAM)
        client.connect((config["IP"],config["Port"]))
        LenBuffer = client.recv(4)
        Len = LenBuffer[0] + (LenBuffer[1] << 8) + (LenBuffer[2] << 16) + (LenBuffer[3] << 24)
        logging.info("MotorServer" + ": Receive Photo Size: %s!", str(Len))
        RecvBuffer = bytearray()
        RecvLen = 0
        while RecvLen < Len:
            buffer = client.recv(4 * 1024)
            RecvBuffer[len(RecvBuffer):] = buffer
            RecvLen += len(buffer)
            logging.info("MotorServer" + ": Receive Data Size: %s/%s!", str(len(buffer)), str(RecvLen))
        logging.info("MotorServer" + ": The Photo was Received!")
        client.close()
        file = open("abcd.jpg", "wb")
        file.write(RecvBuffer)
        file.close()

# -*- coding:utf-8 -*-
from socket import *


# 模拟APP客户端控制小车
class MotorCarClient:
    clientMessage = '''{"MESSAGE_ID":11111,"CAR_CONTROL":{"ANGLE":180,"RANGE":0.85},"HANDS_CONTROL":[-0.9,0,0,0]}'''

    def __init__(self, config, logging):
        self.MESSAGE_LEN = 256
        self.tag = "MotorCar Client"
        self.logging = logging
        clientConnection = socket(AF_INET, SOCK_STREAM)
        clientConnection.connect((config["IP"], config["Port"]))
        clientConnection.send(bytes(MotorCarClient.clientMessage.ljust(self.MESSAGE_LEN, ' '), encoding='utf-8'))
        ReBuffer = bytearray()
        try:
            while True:
                if (len(ReBuffer) >= self.MESSAGE_LEN):
                    buffer = ReBuffer[0:self.MESSAGE_LEN]
                    ReString = str(buffer, encoding="utf-8")
                    self.logging.info(self.tag + ": New Message!\n"+ReString)
                    ReBuffer = ReBuffer[self.MESSAGE_LEN:]
                    break
                else:
                    recvBuffer = clientConnection.recv(self.MESSAGE_LEN)
                    ReBuffer[len(ReBuffer):] = recvBuffer
        except socket.error as msg:
            self.logging.waring(self.tag + ": Closed Client Connection:%s!")
        finally:
            clientConnection.close()

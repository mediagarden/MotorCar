# -*- coding:utf-8 -*-
import threading
import socket
import logging
from MotorCarServer.MessageProcess import MessageProcess


class MotorCarServer:
    def __init__(self, config, logging):
        self.logging = logging
        self.config = config
        self.serverSocket = None
        self.serverThread = None
        # 数据包的长度
        self.MESSAGE_LEN = 256
        self.tag = "MotorCar Server"
        self.messageProcess = MessageProcess(logging)

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
        self.logging.info(self.tag + ": New Client Connection:%s!", str(client["Address"]))
        clientConnection = client["Connection"]
        ReBuffer = bytearray()
        try:
            while True:
                if (len(ReBuffer) >= self.MESSAGE_LEN):
                    buffer = ReBuffer[0:self.MESSAGE_LEN]
                    ReString = str(buffer, encoding="utf-8")
                    self.logging.info(self.tag + ": Request Message!\n" + ReString)
                    # Message Processing
                    StString = self.messageProcess.porcess(ReString)
                    # Message Processing
                    StString = StString.ljust(self.MESSAGE_LEN, ' ')
                    buffer = bytes(StString, encoding="utf-8")
                    clientConnection.send(buffer)
                    self.logging.info(self.tag + ": Response Message!\n" + StString)
                    ReBuffer = ReBuffer[self.MESSAGE_LEN:]
                else:
                    recvBuffer = clientConnection.recv(1024)
                    ReBuffer[len(ReBuffer):] = recvBuffer
        except socket.error as msg:
            self.logging.warning(self.tag + ": Closed Client Connection:%s!", str(client["Address"]))
        finally:
            clientConnection.close()
            self.clientInfo.remove(client)

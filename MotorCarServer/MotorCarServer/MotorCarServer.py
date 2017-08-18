# -*- coding:utf-8 -*-
import threading
import socket
from MotorCarServer.MessageProcess import MessageProcess

class MotorCarServer:
    def __init__(self,config):
        self.config = config
        self.serverSocket = None
        self.serverThread = None
        #数据包的长度
        self.MESSAGE_LEN = 128
    def run(self):
        self.serverThread = threading.Thread(target=self.__runServer)
        self.serverThread.setDaemon(True)
        self.serverThread.start()

    def __runServer(self):
        print("MotorCar Server: Socket Server Running!")
        self.serverSocket = socket.socket()
        self.serverSocket.bind((self.config["IP"], self.config["Port"]))
        self.serverSocket.listen(5)
        self.clientInfo = []
        while(True):
            conn, address = self.serverSocket.accept()
            client = {"Connection": conn, "Address": address, "Thread":"None"}
            clientThread = threading.Thread(target=self.__clientServer,args=(client,))
            client["Thread"] = clientThread
            clientThread.setDaemon(True)
            clientThread.start()
            self.clientInfo.append(client)
    def __clientServer(self,client):
        print("MotorCar Server: New Client Connection:"+str(client["Address"]))
        clientConnection = client["Connection"]
        ReBuffer = bytearray()
        try:
            while True:
                if(len(ReBuffer) >= self.MESSAGE_LEN):
                    buffer = ReBuffer[0:self.MESSAGE_LEN]
                    ReString = str(buffer, encoding="utf-8")
                    print("MotorCar Server: New Message")
                    print(ReString)
                    ##Message Processing
                    StString=MessageProcess.porcess(ReString)
                    ##Message Processing
                    StString=StString.ljust(self.MESSAGE_LEN,' ')
                    buffer = bytes(StString, encoding="utf-8")
                    clientConnection.send(buffer)
                    print("MotorCar Server: Return Message")
                    print(StString)
                    ReBuffer=ReBuffer[self.MESSAGE_LEN:]
                else:
                    recvBuffer = clientConnection.recv(1024)
                    ReBuffer[len(ReBuffer):] = recvBuffer
                    # ReBuffer.append(bytearray(recvBuffer))
        except socket.error as msg:
            print("MotorCar Server: Closed Client Connection:" + str(client["Address"]))
            clientConnection.close()
            self.clientInfo.remove(client)

# -*- coding:utf-8 -*-
from MotorCarServer.MotorCarServer import MotorCarServer
from time import ctime,sleep

def getConfig():
    config = {'IP': '0.0.0.0', 'Port': 10000}
    return config

if __name__ == '__main__':
    print("MotorCar Server: MotorCar Server Running!")
    print("MotorCar Server: Please Using Python3!")
    server = MotorCarServer(getConfig())
    server.run()
    while True:
        sleep(1)

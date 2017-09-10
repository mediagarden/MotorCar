# -*- coding:utf-8 -*-
from MotorCarServer.MotorCarServer import MotorCarServer
from MotorCarServer.MotorCarClient import MotorCarClient
from CameraServer.CameraClient import CameraClient
from CameraServer.CameraServer import CameraServer
from time import ctime, sleep

import logging


def getMotorServerConfig():
    config = {'IP': '0.0.0.0', 'Port': 10000}
    return config


def getCameraServerConfig():
    config = {'IP': '0.0.0.0', 'Port': 10001}
    return config


if __name__ == '__main__':
    # 配置日志信息
    logging.basicConfig(level=logging.DEBUG,
                        format='%(asctime)s %(filename)s[line:%(lineno)d] %(levelname)s %(message)s',
                        datefmt='%a, %d %b %Y %H:%M:%S',
                        filename='MotorCarServer.log',
                        filemode='w')
    # 定义一个Handler打印INFO及以上级别的日志到sys.stderr
    console = logging.StreamHandler()
    console.setLevel(logging.INFO)
    # 设置日志打印格式
    formatter = logging.Formatter('%(name)-12s: %(levelname)-8s %(message)s')
    console.setFormatter(formatter)
    # 将定义好的console日志handler添加到root logger
    logging.getLogger('').addHandler(console)

    logging.info("MotorCar Server: Please Using Python3!")
    logging.info("MotorCar Server: MotorCar Server Running!")
    motorCarServer = MotorCarServer(getMotorServerConfig(), logging)
    motorCarServer.run()
    # config = {'IP': '127.0.0.1', 'Port': 10000}
    # MotorCarClient(config, logging)

    logging.info("Camera Server: MotorCar Server Running!")
    cameraServer = CameraServer(getCameraServerConfig(), logging)
    cameraServer.run()
    # 模拟APP客户端获取照片
    #config = {'IP': '127.0.0.1', 'Port': 10001}
    #CameraClient(config, logging)

while True:
    sleep(1)

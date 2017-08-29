# -*- coding:utf-8 -*-
import json

from MotorCarControl.MotorCarControl import MotorCarControl
from MotorCarControl.HandControl import HeadControl

'''
REQUEST:
{
    "MESSAGE_ID":11111,
    "CAR_CONTROL":{
        "ANGLE":180,
        "RANGE":0.85
    },
    "HANDS_CONTROL":[
        -0.9,
        0.0,
        0.0,
        0.0
    ]
}
RESPONSE:
{
    "MESSAGE_ID":11111,
    "STATUS":0
}
'''


class MessageProcess:
    def __init__(self, logging):
        self.logging = logging
        self.tag = "Message Process"
        self.motorCarControl = MotorCarControl(logging)
        self.motorCarControl.run()
        self.headControl = HeadControl(logging)
        self.headControl.run()

    def porcess(self, requestMsg):
        self.logging.info(self.tag + ": Process The Message!")
        self.logging.info(requestMsg)
        requestMsg = requestMsg.strip()
        requestMsg = json.loads(requestMsg)
        self.logging.info(self.tag + ": Process The Message!")
        Ret = self.motorCarControl.putControlMessage(requestMsg)
        Ret &= self.headControl.putControlMessage(requestMsg)
        if (Ret):
            responseMsg = {"MESSAGE_ID": requestMsg["MESSAGE_ID"], "STATUS": 0}
        else:
            responseMsg = {"MESSAGE_ID": requestMsg["MESSAGE_ID"], "STATUS": -1}
        return json.dumps(responseMsg)

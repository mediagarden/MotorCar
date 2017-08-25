# -*- coding:utf-8 -*-
import json

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
    def porcess(Msg):
        Msg = Msg.strip()
        msg = json.loads(Msg)


        msg = {"MESSAGE_ID": 11111, "STATUS": 0}
        # if (str(msg["Cmd"]) == "CAR_STOP"):
        #    msg = {"Status": True}
        #    return json.dumps(msg)

        return json.dumps(msg)
        # json.dumps#json.loads

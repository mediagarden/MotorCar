# -*- coding:utf-8 -*-
import json
class MessageProcess:
    def porcess(Msg):
        msg=json.loads(Msg)
        if(str(msg["Cmd"])=="CAR_STOP"):
            msg = {"Status":True}
            return json.dumps(msg)
        
        return Msg
        #json.dumps#json.loads
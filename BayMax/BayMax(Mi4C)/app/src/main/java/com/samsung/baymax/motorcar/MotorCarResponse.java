package com.samsung.baymax.motorcar;

import org.json.JSONException;
import org.json.JSONObject;

class MotorCarResponse {
    private int message_id;

    private int status;

    public static MotorCarResponse DeSerialize(String msg) {
        int message_id = 0;
        int status = 0;
        try {
            JSONObject json = new JSONObject(msg);
            message_id = (int)json.get("MESSAGE_ID");
            status = (int)json.get("STATUS");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new MotorCarResponse(message_id,status);
    }

    private MotorCarResponse(int message_id,int status)
    {
        this.message_id=message_id;
        this.status=status;
    }
    public int getMessage_id() {
        return message_id;
    }

    public int getStatus() {
        return status;
    }
}

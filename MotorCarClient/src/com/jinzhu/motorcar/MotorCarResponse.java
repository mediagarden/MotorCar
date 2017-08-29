package com.jinzhu.motorcar;

import net.sf.json.JSONObject;

public class MotorCarResponse {
	private int message_id;

	private int status;

	public static MotorCarResponse DeSerialize(String msg) {
		JSONObject json=JSONObject.fromObject(msg);
		int message_id=(int)json.get("MESSAGE_ID");
		int status=(int)json.get("STATUS");
		
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

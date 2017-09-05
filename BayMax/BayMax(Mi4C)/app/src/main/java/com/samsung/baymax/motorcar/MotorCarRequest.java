package com.samsung.baymax.motorcar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MotorCarRequest {

	private static int message_id = 1;

	private double angle;  //角度
	private double range;  //幅度
	private double x;
	private double y;
	private double z;
	private double h;

	/**
	 * ����С������̬������Ϣ
	 * 
	 * @param d
	 *            ��ת�Ƕ�
	 * @param e
	 *            �ٶ�
	 */
	public void setCarControl(double d, double e) {
		this.angle = d;
		this.range = e;
	}

	/**
	 * ���û�е�ֵ���̬��Ϣ
	 * 
	 * @param x
	 *            ǰ��
	 * @param y
	 *            ����
	 * @param z
	 *            ����
	 * @param h
	 *            ץ��
	 */
	public void setHandControl(double x, double y, double z, double h) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.h = h;
	}

	public String Serialize() {
		JSONObject json = new JSONObject();

		try {
			json.put("MESSAGE_ID", message_id++);
			JSONObject carControl = new JSONObject();
			carControl.put("ANGLE", angle);
			carControl.put("RANGE", range);
			json.put("CAR_CONTROL", carControl);

			JSONArray handControl = new JSONArray();

			handControl.put(x);
			handControl.put(y);
			handControl.put(z);
			handControl.put(h);

			json.put("HANDS_CONTROL", handControl);
		} catch (JSONException e) {
			e.getStackTrace();
		}

		return json.toString();
	}
}

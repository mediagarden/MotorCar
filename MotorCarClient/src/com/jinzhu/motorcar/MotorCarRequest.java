package com.jinzhu.motorcar;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MotorCarRequest {

	private static int message_id = 1;

	private double angle;
	private double range;
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

		json.put("MESSAGE_ID", message_id++);
		JSONObject carControl = new JSONObject();
		carControl.put("ANGLE", angle);
		carControl.put("RANGE", range);
		json.put("CAR_CONTROL", carControl);

		JSONArray handControl = new JSONArray();

		handControl.add(x);
		handControl.add(y);
		handControl.add(z);
		handControl.add(h);

		json.put("HANDS_CONTROL", handControl);

		return json.toString();
	}
}

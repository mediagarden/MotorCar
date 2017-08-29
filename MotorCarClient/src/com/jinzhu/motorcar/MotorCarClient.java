package com.jinzhu.motorcar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONStringer;

public class MotorCarClient extends TimerTask implements ICameraCallback,IMotorCarCallback{
	
	private static MotorCar motorCar=MotorCar.getinstance();
	private static MotorCarClient motorCarClient = new MotorCarClient();
	//private static int messageID=1;
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		
		//拍照示例//
		Camera camera = Camera.getinstance();
		camera.setCameraCallback(motorCarClient);// 设置通知回调函数
		camera.takePhoto();// 触发拍照
		
		
		//传递控制手势示例//
		motorCar.setMotorCarCallback(motorCarClient);
		Timer timer = new Timer();
		timer.schedule(motorCarClient, 1000,1000);//暂定每秒钟10次传输
		
		while (true) {
			Thread.sleep(1000);
		}
	}

	@Override
	public void cameraCallback(byte[] photo) {

		if (photo != null) {
			// 获取图片成功,显示图片
			FileOutputStream out = null;
			try {
				out = new FileOutputStream(new File("abcd.jpg"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				out.write(photo);
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Camera:Get Photo Success...");
		}
	}

	@Override
	public void motorCarCallback(String msg) {
		if(msg!=null)
		{
			MotorCarResponse response=MotorCarResponse.DeSerialize(msg);
			//获取服务端返回的数据
			System.out.println("MotorCar:Receive Message...");
			System.out.println(msg);

		}
	}

	@Override
	public void run() {
		/*传递控制手势示例*/
		MotorCar motorCar=MotorCar.getinstance();
		MotorCarRequest request=new MotorCarRequest();
		request.setCarControl(0.0, 1.0);
		request.setHandControl(0.0, 0.0,0.0, 0.1);
		String msg=request.Serialize();
		System.out.println("MotorCar:Send Message...");
		System.out.println(msg);
		motorCar.putmsg(msg);
		
	}

}

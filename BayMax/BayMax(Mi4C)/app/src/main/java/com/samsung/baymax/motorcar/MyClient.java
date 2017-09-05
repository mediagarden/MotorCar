package com.samsung.baymax.motorcar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.samsung.baymax.ShowPhotoActivity;
import com.samsung.baymax.utils.MotorCarControl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by STMC-LH on 2017/8/30.
 */

public class MyClient extends TimerTask implements ICameraCallback, IMotorCarCallback{
    private final String TAG = MotorCar.class.getSimpleName();
    private static MotorCar motorCar = MotorCar.getInstance();
    public MotorCarControl motorCarControl = MotorCarControl.getInstance();
    private static MyClient myClient = null;
    private Timer timer=null;
    private int connect = 0;
    private int cnt = 0;
    public Bitmap bitmap;
    private Context context;
    private Handler handler;

    public static MyClient getInstance(Context context, Handler handler) {
        // 单例模式
        if (myClient == null) {
            myClient = new MyClient(context, handler);
        }
        return myClient;
    }

    public MyClient(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
    }

    public void connectToMotorCar() {
        Log.i(TAG, "MyClient connectToMotorCar");

        if(timer==null)
        {
            //传递控制手势示例//
            motorCar.setMotorCarCallback(this);
            timer = new Timer();
            timer.schedule(this, 1000, 100);//暂定每秒钟10次传输
        }
    }
    public void showActivity() {
        Intent intent = new Intent();
        intent.setClass(context, ShowPhotoActivity.class);
        context.startActivity(intent);
    }

    public void takePhoto() {
        Log.i(TAG, "MyClient takePhoto");
        //拍照示例//
        Camera camera = Camera.getInstance();
        camera.setCameraCallback(this);// 设置通知回调函数
        camera.takePhoto();// 触发拍照
    }

    private Bitmap convertToBitmap(byte[] photo) {
        if(photo.length != 0) {
            return BitmapFactory.decodeByteArray(photo, 0, photo.length);
        } else {
            return null;
        }
    }

    @Override
    public void cameraCallback(byte[] photo) {
        if (photo != null) {
            if (photo.length != 0) {
                bitmap = convertToBitmap(photo);
                Intent intent = new Intent();
                intent.setClass(context, ShowPhotoActivity.class);
                context.startActivity(intent);
            }
        }
    }

    @Override
    public void motorCarCallback(String msg) {
        if(msg!=null)
        {
            MotorCarResponse response = MotorCarResponse.DeSerialize(msg);
            //获取服务端返回的数据
            Log.i(TAG, "MyClient motorCarCallback");
            if(connect == 0)
                connect++;
            if(connect == 1) {
                connect = -1;
                handler.sendEmptyMessage(1000);
            }
        }
    }

    @Override
    public void run() {
		/*传递控制手势示例*/
        MotorCar motorCar = MotorCar.getInstance();
        MotorCarRequest request = new MotorCarRequest();
        request.setCarControl(motorCarControl.angle, motorCarControl.range);
        request.setHandControl(motorCarControl.x , motorCarControl.y, motorCarControl.z, motorCarControl.h);

        if(cnt == 60000)
            cnt = 0;
        cnt++;
        if (cnt%20 == 0) {
    //        handler.sendEmptyMessage(1001);
        }
        Log.i(TAG, "MyClient run: angle: " + motorCarControl.angle + " , range: " +  motorCarControl.range + " , x: " + motorCarControl.x + ", y: " +  motorCarControl.y + " , z: " +  motorCarControl.z + " , h: " + motorCarControl.h);
        String msg = request.Serialize();
        motorCar.putmsg(msg);
    }
}

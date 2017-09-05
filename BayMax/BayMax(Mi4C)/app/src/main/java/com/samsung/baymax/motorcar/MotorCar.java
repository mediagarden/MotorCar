package com.samsung.baymax.motorcar;

import android.util.Log;

import com.samsung.baymax.utils.Config;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class MotorCar extends Thread {
    private final static String TAG = MotorCar.class.getSimpleName();
    private IMotorCarCallback callback = null;
    private Semaphore sem = null;
    private static MotorCar motorCar = null;

    // 创建一个线程安全的队列用于保存消息请求
    private BlockingQueue<String> queue = null;
    // 定义信号量用于控制线程运行状态

    private Socket sock = null;
    private final int MESSAGE_LEN = 256;

    public static MotorCar getInstance() {
        // 单例模式
        if (motorCar == null) {
            Log.i(TAG, "MotorCar init1");
            motorCar = new MotorCar();
        }
        return motorCar;
    }

    public MotorCar() {
        Log.i(TAG, "MotorCar init2");
        queue = new LinkedBlockingQueue<String>();
        sem = new Semaphore(0);
        this.start();
    }

    public void run() {

        while (true) {
            // 失败则放弃该次操作，但不退出线程
            try {
                Log.i(TAG, "MotorCar run");
                sem.acquire();
                if (!queue.isEmpty()) {
                    String msg;
                    try {
                        msg = messageProcess(queue.poll());
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        this.sock=null;
                        if (this.callback != null) {
                            callback.motorCarCallback(null);
                        }
                        continue;
                    }
                    if (this.callback != null) {
                        this.callback.motorCarCallback(msg);
                    }
                }
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                if (this.callback != null) {
                    this.callback.motorCarCallback(null);
                }
                continue;
            }
        }
    }

    /***
     * 传递消息
     *
     * @return
     * @throws UnknownHostException
     * @throws IOException
     */
    private String messageProcess(String msg) throws UnknownHostException, IOException {
        if(this.sock==null||!this.sock.isConnected())
        {
            //System.out.println("new Socket...");
            sock=new Socket(Config.HostIP, Config.MotorCarPort);
        }
        String fomartstr="%-"+Integer.toString(MESSAGE_LEN)+"s";
        msg = String.format(fomartstr, msg);
        sock.getOutputStream().write(msg.getBytes("UTF-8"));
        byte[] buffer=new byte[MESSAGE_LEN];
        int readlen=0;
        while(readlen<MESSAGE_LEN)
        {
            readlen+=sock.getInputStream().read(buffer, readlen, MESSAGE_LEN-readlen);
        }
        return 	new String(buffer,"UTF-8");
    }

    public void setMotorCarCallback(IMotorCarCallback callback) {
        this.callback = callback;
        return;
    }

    /***
     * 请求消息API
     *
     * @param Message
     */
    public void putmsg(String Message) {
        try {
            Log.i(TAG, "MotorCar putmsg");
            queue.put(Message);
            sem.release();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            if (this.callback != null) {
                callback.motorCarCallback(null);
            }
        }
    }
}
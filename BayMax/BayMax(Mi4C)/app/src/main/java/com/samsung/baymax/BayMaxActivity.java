package com.samsung.baymax;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.samsung.baymax.motorcar.MotorCar;
import com.samsung.baymax.motorcar.MyClient;
import com.samsung.baymax.utils.MotorCarControl;

import java.text.DecimalFormat;

public class BayMaxActivity extends Activity {

    private final String TAG = BayMaxActivity.class.getSimpleName();
    private TextView connectDevice;
    private TextView takePhotos;
    private TextView showDirection;

    private LinearLayout linearLayout;
    private LinearLayout firstButton;
    private LinearLayout secondButton;
    private LinearLayout thirdButton;
    private LinearLayout forthButton;
    private boolean isConnected = false;
    private int cnt = 0;

    public Handler baymaxHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1000) {
                isConnected = true;
                Toast.makeText(getBaseContext(), "设备连接成功", Toast.LENGTH_LONG).show();
                if (isConnected)
                    connectDevice.setOnClickListener(null);
            } else if (msg.what == 1001) {
                showDirection.setText( getString(R.string.direction_info) + " angle: " + stringFomate(myClient.motorCarControl.angle) + " , range: " +  stringFomate(myClient.motorCarControl.range) + " , x: " + stringFomate(myClient.motorCarControl.x) + ", y: "
                        +  stringFomate(myClient.motorCarControl.y) + " , z: " +  stringFomate(myClient.motorCarControl.z) + " , h: " + stringFomate(myClient.motorCarControl.h));
            //    Toast.makeText(getBaseContext(), "MyClient run: angle: " + myClient.motorCarControl.angle + " , range: " +  myClient.motorCarControl.range + " , x: " + myClient.motorCarControl.x + ", y: "
             //           +  myClient.motorCarControl.y + " , z: " +  myClient.motorCarControl.z + " , h: " + myClient.motorCarControl.h, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private String stringFomate(double num) {
        String str = "";
        DecimalFormat df = new DecimalFormat("0.00");
        return str + df.format(num);
    }

    private MyClient myClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bay_max);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏

        linearLayout = findViewById(R.id.root);
        connectDevice = findViewById(R.id.button_connect_to_device);
        takePhotos = findViewById(R.id.button_take_photos);
        showDirection = findViewById(R.id.show_direction);

        firstButton = findViewById(R.id.layout_first_button);
        secondButton = findViewById(R.id.layout_second_button);
        thirdButton = findViewById(R.id.layout_third_button);
        forthButton = findViewById(R.id.layout_forth_button);

        connectDevice.setOnClickListener(connectDeviceListener);
        takePhotos.setOnClickListener(takePhotosListener);

        //左侧方向，速度码盘
        final  DrawView drawView = new DrawView(this, 125, 125, 50);
        myClient = MyClient.getInstance(getApplicationContext(), baymaxHandler);
        myClient.motorCarControl.angle = 0;
        myClient.motorCarControl.range = 0;
        final double maxLength = getBaseContext().getResources().getDisplayMetrics().density * 75;
        final double para = getBaseContext().getResources().getDisplayMetrics().density * 125;
        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        drawView.currentX = event.getX();
                        drawView.currentY = event.getY();
                        float circleX = drawView.currentX > drawView.getInitX() ? (drawView.currentX - drawView.getInitX()) : (drawView.getInitX() - drawView.currentX);
                        float circleY = drawView.currentY > drawView.getInitY() ? (drawView.currentY - drawView.getInitY()) : (drawView.getInitY() - drawView.currentY);
                        float radius = drawView.getInitX() - drawView.getRadius();
                        if (circleX * circleX + circleY * circleY > radius * radius) {
                            float length = (float)Math.sqrt((double)(circleX * circleX + circleY * circleY));
                            circleX = (radius * circleX) / length;
                            circleY = (radius * circleY) / length;
                            drawView.currentX = drawView.currentX > drawView.getInitX() ? (drawView.getInitX() + circleX) : (drawView.getInitX() - circleX);
                            drawView.currentY = drawView.currentY > drawView.getInitY() ? (drawView.getInitY() + circleY) : (drawView.getInitY() - circleY);
                        }
                        double x = drawView.currentX - para;
                        double y = drawView.currentY - para;
                        double z = Math.sqrt(x * x + y * y);
                        myClient.motorCarControl.range = z / maxLength;

                        if (x==0 && y<0) {
                            myClient.motorCarControl.angle = 0;
                        } else if(x>0 && y<0) {
                            myClient.motorCarControl.angle = (Math.atan(-x/y)) / (2 * Math.PI);
                            Log.i(TAG, "circle length: atan angle: "+ myClient.motorCarControl.angle  +  ", x: " + x + " y: " + y);
                        } else if (y==0 && x>0) {
                            myClient.motorCarControl.angle = 90 /360;
                        } else if (x>0 && y>0) {
                            myClient.motorCarControl.angle =0.5 - Math.atan(x/y) / (2 * Math.PI);
                        } else if (x==0 && y>0) {
                            myClient.motorCarControl.angle = 180/360;
                        } else if (x<0 && y>0) {
                            myClient.motorCarControl.angle = 0.5 + Math.atan(-x/y) /  (2 * Math.PI);
                        } else if (x<0 && y==0) {
                            myClient.motorCarControl.angle = 270/360;
                        } else if (x<0 && y<0) {
                            myClient.motorCarControl.angle = 1 - Math.atan(x/y) /  (2 * Math.PI);
                        }
                        showDirection.setText(" angle: " + stringFomate(myClient.motorCarControl.angle) + " , range: " +  stringFomate(myClient.motorCarControl.range) + " , x: " + stringFomate(myClient.motorCarControl.x) + ", y: "
                                +  stringFomate(myClient.motorCarControl.y) + " , z: " +  stringFomate(myClient.motorCarControl.z) + " , h: " + stringFomate(myClient.motorCarControl.h));
                        Log.i(TAG, "circle length: angle: "+ myClient.motorCarControl.angle  +  ", x: " + x + " y: " + y + " z: " + z + " current X: " + drawView.currentX + " currentY: " + drawView.currentY);
                        //通过draw组件重绘
                        drawView.invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        drawView.currentX = drawView.getInitX();
                        drawView.currentY = drawView.getInitY();
                        myClient.motorCarControl.angle = 0;
                        myClient.motorCarControl.range = 0;
                        showDirection.setText( " angle: " + stringFomate(myClient.motorCarControl.angle) + " , range: " +  stringFomate(myClient.motorCarControl.range) + " , x: " + stringFomate(myClient.motorCarControl.x) + ", y: "
                                +  stringFomate(myClient.motorCarControl.y) + " , z: " +  stringFomate(myClient.motorCarControl.z) + " , h: " + stringFomate(myClient.motorCarControl.h));
                        //通过draw组件重绘
                        drawView.invalidate();
                        break;
                }
                return true;
            }
        });
        linearLayout.addView(drawView);
        addFirstSquare();
        addSecondSquare();
        addThirdSquare();
        addForthSquare();

        showDirection.setText( " angle: " + stringFomate(myClient.motorCarControl.angle) + " , range: " +  stringFomate(myClient.motorCarControl.range) + " , x: " + stringFomate(myClient.motorCarControl.x) + ", y: "
                +  stringFomate(myClient.motorCarControl.y) + " , z: " +  stringFomate(myClient.motorCarControl.z) + " , h: " + stringFomate(myClient.motorCarControl.h));
    }

    public void addFirstSquare() {
        final DrawView drawView = new DrawView(this, 25, 130, 25);
        myClient.motorCarControl.x = 0;
        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        drawView.currentY = event.getY();
                        if (drawView.currentY < drawView.getRadius())
                            drawView.currentY = drawView.getRadius();
                        else if (drawView.currentY > getBaseContext().getResources().getDisplayMetrics().density * 260 -  drawView.getRadius())
                            drawView.currentY = getBaseContext().getResources().getDisplayMetrics().density * 260 -  drawView.getRadius();
                        myClient.motorCarControl.x = (getBaseContext().getResources().getDisplayMetrics().density * 130 - drawView.currentY) / (getBaseContext().getResources().getDisplayMetrics().density * 105);
                        showDirection.setText( " angle: " + stringFomate(myClient.motorCarControl.angle) + " , range: " +  stringFomate(myClient.motorCarControl.range) + " , x: " + stringFomate(myClient.motorCarControl.x) + ", y: "
                                +  stringFomate(myClient.motorCarControl.y) + " , z: " +  stringFomate(myClient.motorCarControl.z) + " , h: " + stringFomate(myClient.motorCarControl.h));
                        drawView.invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        drawView.currentY = drawView.getInitY();
                        myClient.motorCarControl.x = 0;
                        showDirection.setText( " angle: " + stringFomate(myClient.motorCarControl.angle) + " , range: " +  stringFomate(myClient.motorCarControl.range) + " , x: " + stringFomate(myClient.motorCarControl.x) + ", y: "
                                +  stringFomate(myClient.motorCarControl.y) + " , z: " +  stringFomate(myClient.motorCarControl.z) + " , h: " + stringFomate(myClient.motorCarControl.h));
                        //通过draw组件重绘
                        drawView.invalidate();
                        break;
                }
                return true;
            }
        });
        firstButton.addView(drawView);
    }

    public void addSecondSquare() {
        final DrawView drawView = new DrawView(this, 25, 130, 25);
        myClient.motorCarControl.y = 0;
        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                    //    drawView.currentX = event.getX();
                        drawView.currentY = event.getY();
                        if (drawView.currentY < drawView.getRadius())
                            drawView.currentY = drawView.getRadius();
                        else if (drawView.currentY > getBaseContext().getResources().getDisplayMetrics().density * 260 -  drawView.getRadius())
                            drawView.currentY = getBaseContext().getResources().getDisplayMetrics().density * 260 -  drawView.getRadius();
                        myClient.motorCarControl.y = (getBaseContext().getResources().getDisplayMetrics().density * 130 - drawView.currentY) / (getBaseContext().getResources().getDisplayMetrics().density * 105);
                        showDirection.setText( " angle: " + stringFomate(myClient.motorCarControl.angle) + " , range: " +  stringFomate(myClient.motorCarControl.range) + " , x: " + stringFomate(myClient.motorCarControl.x) + ", y: "
                                +  stringFomate(myClient.motorCarControl.y) + " , z: " +  stringFomate(myClient.motorCarControl.z) + " , h: " + stringFomate(myClient.motorCarControl.h));
                        drawView.invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        drawView.currentY = drawView.getInitY();
                        myClient.motorCarControl.y = 0;
                        showDirection.setText( " angle: " + stringFomate(myClient.motorCarControl.angle) + " , range: " +  stringFomate(myClient.motorCarControl.range) + " , x: " + stringFomate(myClient.motorCarControl.x) + ", y: "
                                +  stringFomate(myClient.motorCarControl.y) + " , z: " +  stringFomate(myClient.motorCarControl.z) + " , h: " + stringFomate(myClient.motorCarControl.h));
                        //通过draw组件重绘
                        drawView.invalidate();
                        break;
                }
                return true;
            }
        });
        secondButton.addView(drawView);
    }

    public void addThirdSquare() {
        final DrawView drawView = new DrawView(this, 25, 130, 25);
        myClient.motorCarControl.z = 0;
        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        //    drawView.currentX = event.getX();
                        drawView.currentY = event.getY();
                        if (drawView.currentY < drawView.getRadius())
                            drawView.currentY = drawView.getRadius();
                        else if (drawView.currentY > getBaseContext().getResources().getDisplayMetrics().density * 260 -  drawView.getRadius())
                            drawView.currentY = getBaseContext().getResources().getDisplayMetrics().density * 260 -  drawView.getRadius();
                        myClient.motorCarControl.z = (getBaseContext().getResources().getDisplayMetrics().density * 130 - drawView.currentY) / (getBaseContext().getResources().getDisplayMetrics().density * 105);
                        showDirection.setText( " angle: " + stringFomate(myClient.motorCarControl.angle) + " , range: " +  stringFomate(myClient.motorCarControl.range) + " , x: " + stringFomate(myClient.motorCarControl.x) + ", y: "
                                +  stringFomate(myClient.motorCarControl.y) + " , z: " +  stringFomate(myClient.motorCarControl.z) + " , h: " + stringFomate(myClient.motorCarControl.h));
                        drawView.invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        drawView.currentY = drawView.getInitY();
                        myClient.motorCarControl.z = 0;
                        showDirection.setText( " angle: " + stringFomate(myClient.motorCarControl.angle) + " , range: " +  stringFomate(myClient.motorCarControl.range) + " , x: " + stringFomate(myClient.motorCarControl.x) + ", y: "
                                +  stringFomate(myClient.motorCarControl.y) + " , z: " +  stringFomate(myClient.motorCarControl.z) + " , h: " + stringFomate(myClient.motorCarControl.h));
                        //通过draw组件重绘
                        drawView.invalidate();
                        break;
                }
                return true;
            }
        });
        thirdButton.addView(drawView);
    }

    public void addForthSquare() {
        final DrawView drawView = new DrawView(this, 25, 130, 25);
        myClient.motorCarControl.h = 0;
        drawView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        //    drawView.currentX = event.getX();
                        drawView.currentY = event.getY();
                        if (drawView.currentY < drawView.getRadius())
                            drawView.currentY = drawView.getRadius();
                        else if (drawView.currentY > getBaseContext().getResources().getDisplayMetrics().density * 260 -  drawView.getRadius())
                            drawView.currentY = getBaseContext().getResources().getDisplayMetrics().density * 260 -  drawView.getRadius();
                        myClient.motorCarControl.h = (getBaseContext().getResources().getDisplayMetrics().density * 130 - drawView.currentY) / (getBaseContext().getResources().getDisplayMetrics().density * 105);
                        showDirection.setText( " angle: " + stringFomate(myClient.motorCarControl.angle) + " , range: " +  stringFomate(myClient.motorCarControl.range) + " , x: " + stringFomate(myClient.motorCarControl.x) + ", y: "
                                +  stringFomate(myClient.motorCarControl.y) + " , z: " +  stringFomate(myClient.motorCarControl.z) + " , h: " + stringFomate(myClient.motorCarControl.h));
                        drawView.invalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        drawView.currentY = drawView.getInitY();
                        myClient.motorCarControl.h = 0;
                        showDirection.setText( " angle: " + stringFomate(myClient.motorCarControl.angle) + " , range: " +  stringFomate(myClient.motorCarControl.range) + " , x: " + stringFomate(myClient.motorCarControl.x) + ", y: "
                                +  stringFomate(myClient.motorCarControl.y) + " , z: " +  stringFomate(myClient.motorCarControl.z) + " , h: " + stringFomate(myClient.motorCarControl.h));
                        //通过draw组件重绘
                        drawView.invalidate();
                        break;
                }
                return true;
            }
        });
        forthButton.addView(drawView);
    }

    //点击连接设备按钮连接到WIFI
    private View.OnClickListener connectDeviceListener = new View.OnClickListener() {
        @Override
        public void onClick(View var1) {
            final SocketSyncTask socketSyncTask = new SocketSyncTask();
            socketSyncTask.execute(100);
        }
    };

    //点击拍照按钮
    private View.OnClickListener takePhotosListener = new View.OnClickListener() {
        @Override
        public void onClick(View var1) {
            Toast.makeText(getBaseContext(), "拍照", Toast.LENGTH_LONG).show();
            final PhotoSyncTask photoSyncTask = new PhotoSyncTask();
            photoSyncTask.execute(100);
    //        myClient.showActivity();
        }
    };

    private class SocketSyncTask extends AsyncTask<Integer, Integer, Integer> {
        SocketSyncTask socketSyncTask;

        public SocketSyncTask getInstatcnce() {
            if(socketSyncTask == null) {
                socketSyncTask = new SocketSyncTask();
            }
            return socketSyncTask;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "SocketSyncTask onPreExecute");
            myClient.connectToMotorCar();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            Log.i(TAG, "SocketSyncTask doInBackground");
            return 1;
        }

        @Override
        protected void onPostExecute(Integer usage) {
            super.onPostExecute(usage);
            Log.i(TAG, "SocketSyncTask onPostExecute");
        }
    }

    private class PhotoSyncTask extends AsyncTask<Integer, Integer, Integer> {
        PhotoSyncTask photoSyncTask;

        public PhotoSyncTask getInstance() {
            if(photoSyncTask == null) {
                photoSyncTask = new PhotoSyncTask();
            }
            return photoSyncTask;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i(TAG, "PhotoSyncTask onPreExecute");
            myClient.takePhoto();
        }

        @Override
        protected Integer doInBackground(Integer... params) {
            Log.i(TAG, "PhotoSyncTask doInBackground");
            return 1;
        }

        @Override
        protected void onPostExecute(Integer usage) {
            super.onPostExecute(usage);
            Log.i(TAG, "PhotoSyncTask onPostExecute");
        }
    }
}

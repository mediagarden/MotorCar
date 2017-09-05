package com.samsung.baymax;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsung.baymax.motorcar.MyClient;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by STMC-LH on 2017/8/31.
 */

public class ShowPhotoActivity extends Activity{
    ImageView photo;
    TextView receivePhotoTime;
    Bitmap bitmap;
    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.show_photo_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// 横屏
        photo = (ImageView) findViewById(R.id.motor_photo);
        receivePhotoTime = (TextView) findViewById(R.id.receive_photo_time);

        bitmap = MyClient.getInstance(getApplicationContext(), null).bitmap;
        if (bitmap != null) {
            SimpleDateFormat time = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
            time.applyPattern("接收时间：" + "MM/dd HH:mm:ss");
            receivePhotoTime.setText(time.format(System.currentTimeMillis()));
           // drawable = new BitmapDrawable(bitmap);
          //  photo.setBackground(drawable);
            photo.setImageBitmap(bitmap);
        } else {
            receivePhotoTime.setText("没有图片");
        }
    }
}

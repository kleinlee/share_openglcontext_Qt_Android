package com.halulu.example;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.WindowManager;
import android.view.View;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.graphics.PixelFormat;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.view.Gravity;
import android.widget.Button;
import android.view.LayoutInflater;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

public class FloatViewManager
{
    private static final String TAG = "FloatViewManager";
    public static boolean isStarted = false;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private GLTestView displayView;
    private ImageView imageView;
    private LayoutInflater layoutInflater;
//    private Context context;

    public void Create(Activity activity) {
        Thread t = Thread.currentThread();
        System.out.println("Create activity" + t.getId());

        Context context = activity.getApplicationContext();
        isStarted = true;
//        (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
//        layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = 500;
        layoutParams.height = 500;
        layoutParams.x = 300;
        layoutParams.y = 300;

        layoutInflater = LayoutInflater.from(context);
//        displayView = layoutInflater.inflate(R.layout.image_display, null);

//        imageView = displayView.findViewById(R.id.image_display_imageview);
//        imageView.setImageResource(R.drawable.pic0002);

        if (displayView == null)
        {
            displayView = new GLTestView(context);
        }

        displayView.setOnTouchListener(new FloatingOnTouchListener());
        showFloatingWindow();
    }

    public void requestRender() {
        if (displayView != null) {
            displayView.requestRender();
        }
    }


    private void showFloatingWindow(){
        windowManager.addView(displayView, layoutParams);
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}

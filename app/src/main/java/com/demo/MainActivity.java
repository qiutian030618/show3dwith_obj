package com.demo;

import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jpct.ObjRenderer;


public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private ObjRenderer objRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置为横屏模式
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //初始化GLSurfaceView
        try {
            mGLSurfaceView = new GLSurfaceView(this);
            objRenderer = new ObjRenderer(this);
            mGLSurfaceView.setRenderer(objRenderer);
            setContentView(mGLSurfaceView);
            mGLSurfaceView.requestFocus();//获取焦点
            mGLSurfaceView.setFocusableInTouchMode(true);//设置为可触控
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

            mGLSurfaceView.onResume();
        } catch (Exception e) {

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {

            mGLSurfaceView.onPause();
        } catch (Exception w) {

        }
    }

    private float xpos = -1;
    private float ypos = -1;

    private float rotateX = 0;
    private float rotateY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xpos = event.getX();
                ypos = event.getY();
                return true;
            case MotionEvent.ACTION_UP:
                xpos = -1;
                ypos = -1;
//                rotateX = 0;
//                rotateY = 0;
                return true;

            case MotionEvent.ACTION_MOVE:

                float xd = event.getX() - xpos;
                float yd = event.getY() - ypos;

                xpos = event.getX();
                ypos = event.getY();

                rotateX += xd / -100;
                rotateY += yd / -100;
                objRenderer.rotate(rotateX,rotateY,0);
                return true;
        }
        return super.onTouchEvent(event);
    }

}

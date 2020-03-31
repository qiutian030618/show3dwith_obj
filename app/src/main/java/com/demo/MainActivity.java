package com.demo;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.jpct.ObjManager;
import com.example.jpct.ObjRenderer;


public class MainActivity extends AppCompatActivity {

    private GLSurfaceView mGLSurfaceView;
    private ObjRenderer objRenderer;

    private String objFileName = "asm0001.obj";
    private String mtlFileName = "asm0001.mtl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mGLSurfaceView = new GLSurfaceView(this);
            objRenderer = new ObjRenderer(this);
            objRenderer.setObjFileName(objFileName);
            objRenderer.setMtlFileName(mtlFileName);
            objRenderer.setScale(0.05f);
            objRenderer.create3dObj();
            mGLSurfaceView.setRenderer(objRenderer);
            setContentView(mGLSurfaceView);
            //获取焦点
            mGLSurfaceView.requestFocus();
            //设置为可触控
            mGLSurfaceView.setFocusableInTouchMode(true);
        } catch (Exception e) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
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

                rotateX += xd;
                rotateY += yd;
                objRenderer.rotate(rotateX, rotateY, 0);
                return true;
        }
        return super.onTouchEvent(event);
    }

}

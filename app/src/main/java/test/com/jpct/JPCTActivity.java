package test.com.jpct;

import android.graphics.Color;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.threed.jpct.Camera;
import com.threed.jpct.FrameBuffer;
import com.threed.jpct.Light;
import com.threed.jpct.Loader;
import com.threed.jpct.Matrix;
import com.threed.jpct.Object3D;
import com.threed.jpct.Primitives;
import com.threed.jpct.SimpleVector;
import com.threed.jpct.World;
import com.threed.jpct.util.MemoryHelper;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @auth qiutian
 * @since 2020-03-27 16:33
 * <p>
 * bug不可怕，就怕bug不解决
 */
public class JPCTActivity extends AppCompatActivity {

    private GLSurfaceView mGlSurfaceView;
    private GIRenderer mGIRenderer;
    private float xpos = -1;
    private float ypos = -1;
    private float rotateX = 0;
    private float rotateY = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mGlSurfaceView = new GLSurfaceView(this);
            mGIRenderer = new GIRenderer();
            mGlSurfaceView.setRenderer(mGIRenderer);
            setContentView(mGlSurfaceView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGlSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGlSurfaceView.onPause();
    }

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
                rotateX = 0;
                rotateY = 0;
                return true;

            case MotionEvent.ACTION_MOVE:

                float xd = event.getX() - xpos;
                float yd = event.getY() - ypos;

                xpos = event.getX();
                ypos = event.getY();

                rotateX = xd / -100;
                rotateY = yd / -100;

                return true;
        }

        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return super.onTouchEvent(event);
    }

    /**
     * @auth qiutian
     * @since 2020-03-27 16:35
     * <p>
     * bug不可怕，就怕bug不解决
     */
    public class GIRenderer implements GLSurfaceView.Renderer {

        private boolean stop = false;
        private Object3D cube;
        private FrameBuffer fb;
        private Light sun;
        private World world;

        public void setStop(boolean stop) {
            this.stop = stop;
        }

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Object3D cube = Primitives.getCube(10);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            if (fb != null) {
                fb.dispose();
            }
            fb = new FrameBuffer(gl, width, height);

            world = new World();

            //设置环境光源强度，负变暗，正照亮
            world.setAmbientLight(100, 100, 100);

            sun = new Light(world);
//            Bitmap image = BitmapFactory.decodeResource(JPCTActivity.this.getResources(), R.mipmap.ic_launcher);

//            Drawable drawable = JPCTActivity.this.getResources().getDrawable(R.mipmap.ic_launcher);
//            Texture texture = new Texture(drawable);
//            String textureName = "texture";
//            TextureManager.getInstance().addTexture(textureName, texture);
            try {
                Object3D[] object3DS = Loader.loadOBJ(getObjFileStream("asm0001.obj"),
                        getObjFileStream("asm0001.mtl"), 1f);
                Object3D object3D = new Object3D(0);
                Object3D temp = null;
                for (int i = 0; i < object3DS.length; i++) {
                    temp = object3DS[i];
                    temp.setCenter(SimpleVector.ORIGIN);
//                    temp.rotateX((float) (-0.5 * Math.PI));
                    temp.rotateMesh();
                    temp.setRotationMatrix(new Matrix());
                    object3D = Object3D.mergeObjects(object3D, temp);
                    object3D.build();
                }
                cube = object3D;
            } catch (Exception e) {
                cube = Primitives.getCube(10);
                Log.e("---","e  "+e.getMessage());
            }

            cube.calcTextureWrapSpherical();

//            cube.setTexture(textureName);

            cube.strip();

            cube.build();

            world.addObject(cube);

            Camera camera = world.getCamera();
            camera.moveCamera(Camera.CAMERA_MOVEOUT, 50);
            camera.lookAt(cube.getTransformedCenter());
            SimpleVector simpleVector = new SimpleVector();
            simpleVector.set(cube.getTransformedCenter());

            simpleVector.x -= 100;
            simpleVector.y -= 100;
            simpleVector.z -= 100;
            sun.setPosition(simpleVector);

            MemoryHelper.compact();
        }

        private InputStream getObjFileStream(String fileName) {
            try {
                return getAssets().open(fileName);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            if (!stop) {
                if (rotateX != 0) {
                    cube.rotateX(rotateX);
                    rotateX = 0;
                }

                if (rotateY != 0) {
                    cube.rotateZ(rotateY);
//                    cube.rotateZ(rotateY);
                    rotateY = 0;
                }

                fb.clear(Color.BLUE);
                world.renderScene(fb);
                world.draw(fb);
                fb.display();
            } else {
                if (fb != null) {
                    fb.dispose();
                    fb = null;
                }
            }
        }
    }
}

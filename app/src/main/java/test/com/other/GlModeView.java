package test.com.other;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.core.view.GestureDetectorCompat;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @auth qiutian
 * @since 2020-03-27 11:58
 * <p>
 * bug不可怕，就怕bug不解决
 */
public class GlModeView extends GLSurfaceView implements GLSurfaceView.Renderer, GestureDetector.OnGestureListener {
    //obj文件解析类
    private ObjLoaderUtil mObjLoaderUtil;
    //手势工具类
    private GestureDetectorCompat mCompat;
    //模型沿x，y轴旋转的角度
    private float xrot, yrot;

//    public GlModeView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context);
//    }

    public GlModeView(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context) {
        setRenderer(this);
        mObjLoaderUtil = new ObjLoaderUtil();
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mObjLoaderUtil.load(context);
                        } catch (Exception e) {
                        }
                    }
                }
        ).start();
        mCompat = new GestureDetectorCompat(context, this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //设置清屏色
        gl10.glClearColor(0.1f, 1f, 1f, 0);
        //用当前光线参数计算顶点颜色。否则仅仅简单将当前颜色与每个顶点关联。
        gl10.glEnable(GL10.GL_LIGHTING);
        //启用服务器端GL功能，允许（apply）引入的颜色与颜色缓冲区中的值进行逻辑运算
        gl10.glEnable(GL10.GL_BLEND);
        //这一行启用smooth shading(阴影平滑)。阴影平滑通过多边形精细的混合色彩，并对外部光进行平滑。
        gl10.glShadeModel(GL10.GL_SMOOTH);
        //当前活动纹理单元为二维纹理
        gl10.glEnable(GL10.GL_TEXTURE_2D);
        //做深度比较和更新深度缓存。值得注意的是即使深度缓冲区存在并且深度mask不是0，如果深度测试禁用的话，深度缓冲区也无法更新。
        gl10.glEnable(GL10.GL_DEPTH_TEST);
        // 法向量被计算为单位向量
        gl10.glEnable(GL10.GL_NORMALIZE);
        //启用面部剔除功能
        gl10.glEnable(GL10.GL_CULL_FACE);
        //指定哪些面不绘制
        gl10.glCullFace(GL10.GL_BACK);
        //开启定义多边形的正面和背面
        gl10.glEnable(GL10.GL_CULL_FACE);
        //改变每个顶点的散射和环境反射材质，可以使用颜色材质。
        gl10.glEnable(GL10.GL_COLOR_MATERIAL);
        // 开启抗锯齿
        gl10.glEnable(GL10.GL_POINT_SMOOTH);
        gl10.glHint(GL10.GL_POINT_SMOOTH_HINT, GL10.GL_NICEST);
        gl10.glEnable(GL10.GL_LINE_SMOOTH);
        gl10.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);
        gl10.glEnable(GL10.GL_POLYGON_OFFSET_FILL);
        gl10.glHint(GL10.GL_POLYGON_SMOOTH_HINT, GL10.GL_NICEST);
        //定义多边形的正面和背面,在一个完全由不透明的密闭surface组成的场景中，多边形的背面永远不会被看到。剔除这些不能显示出来的面可以加速渲染器渲染图像的时间。
        gl10.glFrontFace(GL10.GL_CCW);
        //指明深度缓冲区的清理值
        gl10.glClearDepthf(1.0f);
        gl10.glDepthFunc(GL10.GL_LEQUAL);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        //选择投影矩阵
        gl10.glMatrixMode(GL10.GL_PROJECTION);
        gl10.glViewport(0, 0, i, i1);
        gl10.glLoadIdentity();
        float x = (float) i / i1;
        gl10.glFrustumf(-x, x, -1, 1, 5, 200);
        GLU.gluLookAt(gl10, 0, 0, 50, 0, 0, 0, 0, 1, 0);
        //选择模型观察矩阵
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        try {
            //清除屏幕和深度缓存。
            gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
            gl10.glLoadIdentity();

            Log.e("---", "mObjLoaderUtil.mVertexFloatBuffer  " + mObjLoaderUtil.mVertexFloatBuffer);
            //设置顶点坐标buffer
            gl10.glVertexPointer(3, GL10.GL_FLOAT, 0,
                    mObjLoaderUtil.mVertexFloatBuffer);
            //启用顶点数组
            gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            //沿X轴旋转
            gl10.glRotatef(xrot, 1, 0, 0);
            //沿Y轴旋转
            gl10.glRotatef(yrot, 0, 1, 0);

            gl10.glColor4f(1f, 0f, 0f, 0);
            //模型如果四边形化，i就是加4，模型如果三角化，i就是加3
            for (int i = 0; i <= (mObjLoaderUtil.mSurfaceFloat.length / 3 - 4); i += 4) {
                gl10.glDrawArrays(GL10.GL_TRIANGLE_FAN, i, 4);
            }
        } catch (Exception e) {
            Log.e("===","e  "+e.getMessage());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return mCompat.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        xrot -= v;
        yrot -= v1;
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }
}
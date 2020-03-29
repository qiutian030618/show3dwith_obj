package test.com.other1;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.core.view.GestureDetectorCompat;

import java.io.IOException;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * @auth qiutian
 * @since 2020-03-27 13:58
 * <p>
 * bug不可怕，就怕bug不解决
 */
public class ModeView extends GLSurfaceView implements GLSurfaceView.Renderer, GestureDetector.OnGestureListener {
    private ObjUtils mObjUtils;
    private GestureDetectorCompat mCompat;
    private float xrot, yrot;
    private FloatBuffer colorBuffer;

    public ModeView(Context context) throws IOException {
        super(context);
        mObjUtils = new ObjUtils();
        setRenderer(this);
        mObjUtils.loadObj(context, "assets/ch_t.obj");
        mCompat = new GestureDetectorCompat(context, this);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //设置清屏色
        gl10.glClearColor(1f, 1f, 1f, 0);
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
        gl10.glFrustumf(-x, x, -1, 1, 4, 200);
        GLU.gluLookAt(gl10, 0, 0, 60, 0, 0, 0, 0, 1, 0);
        //选择模型观察矩阵
        gl10.glMatrixMode(GL10.GL_MODELVIEW);
        gl10.glLoadIdentity();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //启用顶点数组
        gl10.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl10.glEnableClientState(GL10.GL_COLOR_ARRAY);
        //清除屏幕和深度缓存。
        gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl10.glLoadIdentity();
        //设置顶点坐标buffer
        gl10.glVertexPointer(3, GL10.GL_FLOAT, 0,
                mObjUtils.mVertexFloatBuffer);
        gl10.glTranslatex(0, -20, 0);
        gl10.glRotatef(xrot, 1, 0, 0);
        gl10.glRotatef(yrot, 0, 1, 0);
        gl10.glPushMatrix();
        gl10.glDrawElements(GL10.GL_TRIANGLES, mObjUtils.mIndexShorts.length,
                GL10.GL_UNSIGNED_SHORT, mObjUtils.mIndexShortBuffer);
        gl10.glDisableClientState(GL10.GL_COLOR_ARRAY);
        //关闭点坐标管道
        gl10.glDisableClientState(GL10.GL_VERTEX_ARRAY);
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
package test.com.other;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * @auth qiutian
 * @since 2020-03-27 11:57
 * <p>
 * bug不可怕，就怕bug不解决
 */
public class ObjLoaderUtil {
    // 存放解析出来的顶点的坐标
    private ArrayList<ObjVertexs> mVertexArrayList;
    //存放解析出来面的索引
    private ArrayList<Integer> mIndexArrayList;
    //存放解析出来的法线坐标
    private ArrayList<ObjVertexs> mNormalArrayList;
    //存放解析出来的法线索引
    private ArrayList<Integer> mIndexNormalArrayList;
    //存放3D模型的顶点坐标数据
    public float[] mSurfaceFloat;
    //存放顶点坐标的floatbuffer
    public FloatBuffer mVertexFloatBuffer;
    //顶点坐标索引buffer
    public FloatBuffer mIndexVertexBuffer;
    //存放法线坐标的floatbuffer
    public FloatBuffer mNormalFloatBuffer;


    public ObjLoaderUtil() {
        mVertexArrayList = new ArrayList();
        mIndexArrayList = new ArrayList<>();
        mNormalArrayList = new ArrayList<>();
        mIndexNormalArrayList = new ArrayList<>();
    }

    /**
     * 将obj文件中的顶点坐标与面索引解析出来，存放集合中
     *
     * @param mContext
     * @throws IOException
     */
    public void load(Context mContext) throws IOException {
        Log.e("===", "load");
        //获取assets文件夹下的obj文件的数据输入流
        InputStream mInputStream = mContext.getResources().getAssets().open("asm0001.obj");
//        InputStream mInputStream = mContext.getClass().getClassLoader().getResourceAsStream("assets/asm0001.obj");
        InputStreamReader mInputStreamReader = new InputStreamReader(mInputStream);
        BufferedReader mBufferedReader = new BufferedReader(mInputStreamReader);
        String temps = null;
        //利用buffer读取流将obj文件的内容读取出来存放在temps
        while ((temps = mBufferedReader.readLine()) != null) {
            if (TextUtils.isEmpty(temps)) continue;
            String[] temp = temps.split("[ ]+");
//            String[] temp = temps.split(" ");
            switch (temp[0].trim()) {
                case "v":
                    ObjVertexs objVertexs = new ObjVertexs(Float.valueOf(temp[1]), Float.valueOf(temp[2]), Float.valueOf(temp[3]));
                    mVertexArrayList.add(objVertexs);
                    continue;
                case "vn":
                    ObjVertexs objVertexs1 = new ObjVertexs(Float.valueOf(temp[1]), Float.valueOf(temp[2]), Float.valueOf(temp[3]));

                    mNormalArrayList.add(objVertexs1);
                    continue;

                case "f":
                    if (temp[1].indexOf("/") == -1) {
                        for (int i = 1; i < temp.length; i++) {
                            mIndexArrayList.add(Integer.valueOf(temp[i]));
                        }
                    } else {
                        for (int i = 1; i < temp.length; i++) {
                            mIndexArrayList.add(Integer.valueOf(temp[i].split("/")[0]));
                        }
                    }
                    continue;
                default:
                    continue;
            }
        }

        Log.e("===", "mVertexFloatBuffer " + mVertexFloatBuffer);
        mSurfaceFloat = getSurfaceFloat(mVertexArrayList, mIndexArrayList);
        mVertexFloatBuffer = makeFloatBuffer(mSurfaceFloat);

        mNormalFloatBuffer = makeFloatBuffer(getSurfaceFloat(mNormalArrayList, mIndexNormalArrayList));
    }

    /**
     * 将解析出来的顶点与索引组合起来，形成一个新的float数组，用于绘制3D模型,并将其返回
     *
     * @param mObjVertexs 存放坐标点的集合
     * @param mIntegers   存放索引的集合
     * @return
     */
    public float[] getSurfaceFloat(ArrayList<ObjVertexs> mObjVertexs, ArrayList<Integer> mIntegers) {
        float[][] mFloats = new float[mIntegers.size()][3];
        float[] surfaceFloat = new float[mIntegers.size() * 3];
        for (int i = 0; i < mIntegers.size(); i++) {
            mFloats[i][0] = mObjVertexs.get(mIntegers.get(i) - 1).x;
            mFloats[i][1] = mObjVertexs.get(mIntegers.get(i) - 1).y;
            mFloats[i][2] = mObjVertexs.get(mIntegers.get(i) - 1).z;
        }
        int i = 0;
        for (float[] floats : mFloats) {
            for (float v : floats) {
                surfaceFloat[i++] = v;
            }
        }
        return surfaceFloat;
    }

    /**
     * 将存放3D模型的float数组，转换为floatbuffer
     *
     * @param mFloats
     * @return
     */
    public FloatBuffer makeFloatBuffer(float[] mFloats) {
        //为float数组分配缓存空间，一个float大小为4个字节
        ByteBuffer mByteBuffer = ByteBuffer.allocateDirect(mFloats.length * 4);
        //规定缓存区的字节顺序为本机字节顺序
        mByteBuffer.order(ByteOrder.nativeOrder());
        //将bytebuffer转换为floatbuffer
        FloatBuffer loatBuffer = mByteBuffer.asFloatBuffer();
        //将float数组填充到floatbuffe中,完成转换
        loatBuffer.put(mFloats);
        //规定缓存区的起始索引
        loatBuffer.position(0);
        return loatBuffer;
    }
}

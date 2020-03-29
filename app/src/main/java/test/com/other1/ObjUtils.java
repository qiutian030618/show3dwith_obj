package test.com.other1;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;

/**
 * @auth qiutian
 * @since 2020-03-27 13:55
 * <p>
 * bug不可怕，就怕bug不解决
 */
public class ObjUtils {
    //存放顶点坐标
    private ArrayList<Point> mVertexArrayList;
    public float[] mVertexFloats;
    public FloatBuffer mVertexFloatBuffer;
    //存放顶点索引
    private ArrayList<Short> mIndexArrayList;
    public short[] mIndexShorts;
    public ShortBuffer mIndexShortBuffer;
    //存放读取的obj文件的信息
    private String temps;


    public ObjUtils() {
        mVertexArrayList = new ArrayList<>();
        mIndexArrayList = new ArrayList<>();
    }

    /**
     * 加载obj文件数据
     */
    public void loadObj(Context context, String path) throws IOException {
        //读取city.obj文件
        InputStream inputStream = context.getClass().getClassLoader().getResourceAsStream(path);
        InputStreamReader mInputStreamReader = new InputStreamReader(inputStream);
        BufferedReader mReader = new BufferedReader(mInputStreamReader);
        //如果读取的文本内容不为空，则一直读取，否则停止
        while ((temps = mReader.readLine()) != null) {
            String[] temp = temps.split(" ");
            //以空格为分割符，将读取的一行temps分裂为数组
            //如果读取到的首元素时"v"，则表示读取到的数据是顶点坐标数据
            if (temp[0].trim().equals("v")) {
                mVertexArrayList.add(new Point(Float.valueOf(temp[1]), Float.valueOf(temp[2]), Float.valueOf(temp[3])));
            }
//            如果读取到的首元素时"f"，则表示读取到的数据是面索引数据
            if (temp[0].trim().equals("f")) {
                if (temp[1].indexOf("/") == -1) {
                    for (int i = 1; i < temp.length; i++) {
                        int t = Integer.valueOf(temp[i]);
                        mIndexArrayList.add((short) t);
                    }
                } else {
                    for (int i = 1; i < temp.length; i++) {
                        int t = Integer.valueOf(temp[i].split("/")[0]) - 1;
                        mIndexArrayList.add((short) t);
                    }
                }
            }

        }
        mReader.close();
        mVertexFloats = getVertex(mVertexArrayList);
        mIndexShorts = getIndex(mIndexArrayList);
        mVertexFloatBuffer = makeFloatBuffer(mVertexFloats);
        mIndexShortBuffer = getBufferFromArray(mIndexShorts);
    }

    /**
     * 获取顶点数组
     */
    private float[] getVertex(ArrayList<Point> pointArrayList) {
        float[][] mFloats = new float[pointArrayList.size()][3];
        float[] surfaceFloat = new float[pointArrayList.size() * 3];
        for (int i = 0; i < pointArrayList.size(); i++) {
            mFloats[i][0] = pointArrayList.get(i).x;
            mFloats[i][1] = pointArrayList.get(i).y;
            mFloats[i][2] = pointArrayList.get(i).z;
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
     * 获取索引数组
     */
    private short[] getIndex(ArrayList<Short> integerArrayList) {
        short[] mFloats = new short[integerArrayList.size()];
        for (int i = 0; i < integerArrayList.size(); i++) {
            mFloats[i] = integerArrayList.get(i);
        }
        return mFloats;
    }

    /**
     * 将float数组转换为buffer
     */
    private FloatBuffer makeFloatBuffer(float[] floats) {
        ByteBuffer mByteBuffer = ByteBuffer.allocateDirect(floats.length * 4);
        mByteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = mByteBuffer.asFloatBuffer();
        floatBuffer.put(floats);
        floatBuffer.position(0);
        return floatBuffer;
    }

    private static ShortBuffer getBufferFromArray(short[] array) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(array.length * 2);
        buffer.order(ByteOrder.nativeOrder());
        ShortBuffer shortBuffer = buffer.asShortBuffer();
        shortBuffer.put(array);
        shortBuffer.position(0);
        return shortBuffer;
    }
}

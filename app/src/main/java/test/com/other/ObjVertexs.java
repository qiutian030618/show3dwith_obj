package test.com.other;

/**
 * @auth qiutian
 * @since 2020-03-27 12:24
 * <p>
 * bug不可怕，就怕bug不解决
 */
public class ObjVertexs {
    public float x;
    public float y;
    public float z;

    public ObjVertexs(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "ObjVertexs{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}

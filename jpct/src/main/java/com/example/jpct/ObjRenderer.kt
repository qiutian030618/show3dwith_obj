package com.example.jpct

import android.graphics.Color
import android.opengl.GLSurfaceView
import com.threed.jpct.*
import com.threed.jpct.util.MemoryHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.PI

/**
 *@auth qiutian
 *@since 2020-03-28 16:14
 *
 *bug不可怕，就怕bug不解决
 */
class ObjRenderer : GLSurfaceView.Renderer {

    var stop = false
    private var cube: Object3D? = null
    private var fb: FrameBuffer? = null
    private var sun: Light? = null
    private var world: World? = null
    private var objManager = ObjManager.getInstance()

    private var rotateX = 0F
    private var rotateY = 0F
    private var rotateZ = 0F

    private var preRotateX = 0f
    private var preRotateY = 0f
    private var preRotateZ = 0f

    private var objFileName = "asm0001.obj"
    private var mtlFileName = "asm0001.mtl"
//
//    init {
//        create3dObj()
//    }

    fun setObjFileName(fileName: String) {
        if (fileName != objFileName) {
            objManager.destroy(objFileName)
        }
        objFileName = fileName
    }

    fun setMtlFileName(fileName: String) {
        mtlFileName = fileName
    }

    fun setScale(scale: Float) {
        objManager.setScale(objFileName, scale)
    }

    fun rotate(x: Float, y: Float, z: Float) {
        rotateX = x / 180 * PI.toFloat()
        rotateY = y / 180 * PI.toFloat()
        rotateZ = z / 180 * PI.toFloat()
    }

    fun create3dObj() {
        objManager.create3dObj(objFileName, mtlFileName)
        cube = objManager.get3dObj(objFileName)
    }

    override fun onDrawFrame(gl: GL10?) {
        try {
            if (!stop) {
                cube?.let { cube ->
                    if (rotateX != 0f) {
                        cube.rotateX(preRotateX)
                        cube.rotateX(rotateX)
                        preRotateX = -rotateX
                        rotateX = 0f
                    }
                    if (rotateY != 0f) {
                        cube.rotateY(preRotateY)
                        cube.rotateY(rotateY)
                        preRotateY = -rotateY
                        rotateY = 0f
                    }
                    if (rotateZ != 0f) {
                        cube.rotateZ(preRotateZ)
                        cube.rotateZ(rotateZ)
                        preRotateZ = -rotateZ
                        rotateZ = 0f
                    }
                }

                fb?.clear(Color.WHITE)
                world?.renderScene(fb)
                world?.draw(fb)
                fb?.display()
            } else {
                fb?.dispose()
            }
        } catch (e: Exception) {
        }
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        cube = objManager.get3dObj(objFileName)
        try {
            fb?.dispose()
            fb = FrameBuffer(gl, width, height)
            world = World()
            world?.setAmbientLight(100, 100, 100)
            sun = Light(world)

            cube?.let { cube ->
                cube.calcTextureWrapSpherical()
                cube.strip()
                cube.build()
                world?.addObject(cube)
                val camera = world?.camera
                camera?.moveCamera(Camera.CAMERA_MOVEOUT, 50F)
                camera?.lookAt(cube.transformedCenter)
                val simpleVector = SimpleVector()
                simpleVector.set(cube.transformedCenter)

                simpleVector.x -= 1000
                simpleVector.y -= 1000
                simpleVector.z -= 1000

                sun?.position = simpleVector
                MemoryHelper.compact()
            }
        } catch (e: Exception) {

        }
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {

    }

}
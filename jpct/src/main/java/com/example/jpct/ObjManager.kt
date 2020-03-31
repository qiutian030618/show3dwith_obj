package com.example.jpct

import android.content.Context
import android.util.Log
import com.threed.jpct.*
import java.io.InputStream
import kotlin.math.PI

/**
 *@auth qiutian
 *@since 2020-03-30 17:19
 *
 *bug不可怕，就怕bug不解决
 */
class ObjManager {

    private constructor()

    private enum class Obj {
        INSTANCE;

        private val instance: ObjManager = ObjManager()

        fun getInstance(): ObjManager {
            return instance
        }

    }

    companion object {
        fun getInstance(): ObjManager {
            return Obj.INSTANCE.getInstance()
        }
    }

    private var objFileName = "asm0001.obj"
    private var mtlFileName = "asm0001.mtl"
    private var context: Context? = null
    private var initX = 0f
    private var initY = 0f
    private var initZ = 0f
    private var object3DMap = mutableMapOf<String, Object3D?>()
    private var objScaleMap= mutableMapOf<String,Float>()
    private var objInitRotateMap= mutableMapOf<String,Triple<Float,Float,Float>>()

    fun init(context: Context) {
        this.context = context
    }

    fun setScale(objFileName: String,scale: Float) {
        objScaleMap[objFileName] = scale
    }

    fun initRotate(objFileName: String,x: Float, y: Float, z: Float) {
        objInitRotateMap[objFileName]= Triple(x,y,z)
    }

    fun create3dObj(objFileName: String, mtlFileName: String) {
        var modelScale=0f
        if (objScaleMap.keys.contains(objFileName)){
            modelScale=objScaleMap[objFileName]!!
        }
        if (modelScale<=0){
            modelScale=1f
        }

        destroy(objFileName)
        val isObj = getInputStreamFromAsset(objFileName)
        val ismtl = getInputStreamFromAsset(mtlFileName)
        val objs = Loader.loadOBJ(
                isObj
                ,
                ismtl,
                modelScale
        )

        var cube = Object3D(0)

        var tempObject3D: Object3D

        for (i in objs.indices) {
            tempObject3D = objs[i]
            tempObject3D.center = SimpleVector.ORIGIN
            tempObject3D.rotateX((initX / 180 * PI).toFloat())
            tempObject3D.rotateY((initY / 180 * PI).toFloat())
            tempObject3D.rotateZ((initZ / 180 * PI).toFloat())
            tempObject3D.rotateMesh()
            tempObject3D.rotationMatrix = Matrix()
            cube = Object3D.mergeObjects(cube, tempObject3D)
            cube?.build()
        }

        Log.e("----", "====  $cube")
        isObj?.close()
        ismtl?.close()
        object3DMap[objFileName] = cube
    }

    fun destroy(objFileName: String) {
        get3dObj(objFileName)?.clearObject()
        get3dObj(objFileName)?.clearShader()
        get3dObj(objFileName)?.clearRotation()
        if (object3DMap.keys.contains(objFileName)){
            object3DMap.remove(objFileName)
        }
    }


    private fun getInputStreamFromAsset(fileName: String): InputStream? {
        return try {
            context?.assets?.open(fileName)
        } catch (e: Exception) {
            null
        }
    }

    fun get3dObj(objFileName: String): Object3D? {
        return if (object3DMap.keys.contains(objFileName)) {
            object3DMap[objFileName]
        } else {
            null
        }
    }


}
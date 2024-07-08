package com.example.a3dmodel

import MyGLRenderer
import android.Manifest
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Display
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.a3dmodel.arithmetic.Delaunay
import com.example.a3dmodel.databinding.ActivityMainBinding
import io.github.jdiemke.triangulation.Triangle2D
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: MyGLRenderer

    private lateinit var binding: ActivityMainBinding

    private val PERMISSION_REQUEST_STORAGE = 1000
    private val PICK_TEXT = 101
    var fileuri: Uri? = null
    lateinit var ArraysT: Array<String>

    var Northing = ArrayList<Double>()
    var Easting = ArrayList<Double>()
    var Elevation = ArrayList<Double>()
    var newFace_pointline = ArrayList<String>()
    var newFace_temp_pointline = ArrayList<String>()

    var minX = 0.0
    var maxX = 0.0
    var minY = 0.0
    var maxY = 0.0
    var differenceX = 0.0
    var differenceY = 0.0
    var differenceXY = 0.0
    var meanofX = 0.0
    var meanofY = 0.0
    var scaleXY = 0.0
    var dw = 0f
    var dh = 0f
    var currentdisplay: Display? = null
    var pixelofX = ArrayList<Double>()
    var pixelofY = ArrayList<Double>()
    var facepoint_inpixel = ArrayList<ArrayList<Double>>()
    var facepoint_inCoordinate = ArrayList<ArrayList<Double>>()
    var final_tempList = ArrayList<Double>()
    var new_finallist= ArrayList<Double>()
    val pointname_list = LinkedHashMap<Int, ArrayList<Double>>()

    private val STORAGE_PERMISSION_CODE = 1

    var trianglelist: List<Triangle2D>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestStoragePermission()
        currentdisplay = windowManager.defaultDisplay
        dw = currentdisplay!!.width.toFloat()
        dh = currentdisplay!!.height.toFloat()

/*        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(2)

        renderer = MyGLRenderer()
        renderer.setPoints(createPoints())
        glSurfaceView.setRenderer(renderer)
        setContentView(glSurfaceView)*/

/*        glSurfaceView = GLSurfaceView(this)
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setRenderer(TriangleRenderer())
        setContentView(glSurfaceView)*/

        binding.load.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Select CSV file"), PICK_TEXT)
        }

    }
    /// Runtime RequestPermission
/*    fun requestpermission() {
        //request permission for Read
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_STORAGE)
        }
    }*/
    //////////////////////////////// Get the Import File Name //////////////////////////////////
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_TEXT && data != null) {
            fileuri = data.data
            readText(getFilePath(fileuri!!))
        }
    }
    /// this method is used for getting file path from uri
    fun getFilePath(uri: Uri): String {
        val filename1: Array<String>
        val fn: String
        val filepath = uri.path
        val filePath1 = filepath!!.split(":").toTypedArray()
        filename1 = filepath.split("/").toTypedArray()
        fn = filename1[filename1.size - 1]
        return Environment.getExternalStorageDirectory().path + "/" + filePath1[1]

    }
    /// reading the land xml file
    fun readText(input: String?): String? {
        val file = File(input)
        val text = StringBuilder()
        try {
            val br = BufferedReader(FileReader(file))
            var line: String?
            while (br.readLine().also { line = it } != null) {
                text.append(line)
                ArraysT = line!!.split("\n").toTypedArray()
                readNorthingEasting()
            }
            br.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return text.toString()
    }
    /// In this part Read the Northing and Easting From .xml files
    fun readNorthingEasting() {
        for (i in ArraysT.indices) {
            if (ArraysT[i].contains("<P id=")) {
                var index = 0
                var index2 = 0
                var st_index = 0
                var index3 = 0
                val point_line = ArraysT[i]
                st_index = point_line.indexOf("\">")
                index = st_index
                while (index < point_line.length) {
                    if (point_line[index] == ' ') {
                        index2 = index + 2
                        while (index2 < point_line.length) {
                            if (point_line[index2] == ' ') {
                                index3 = index2 + 3
                                while (index3 < point_line.length) {
                                    if (point_line[index3] == '<') {
                                        new_finallist.add(java.lang.Double.valueOf(point_line.substring(st_index + 2, index + 1)))
                                        new_finallist.add(java.lang.Double.valueOf(point_line.substring(index + 1, index2 + 1)))
                                        new_finallist.add(java.lang.Double.valueOf(point_line.substring(index2 + 1, index3)))
/*                                        Easting.add(java.lang.Double.valueOf(point_line.substring(st_index + 2, index + 1)))
                                        Northing.add(java.lang.Double.valueOf(point_line.substring(index + 1, index2 + 1)))
                                        Elevation.add(java.lang.Double.valueOf(point_line.substring(index2 + 1, index3)))*/
                                    }
                                    index3++
                                }
                            }
                            index2++
                        }
                    }
                    index++
                }
            }
        }
        Log.d(TAG, "readNorthingEasting:new_finallist "+new_finallist)
        points()
        readfacepoint()
    }

    fun points(){
//        val floatList: List<Float> = new_finallist.map { it.toFloat() }
        trianglelist = Delaunay.doDelaunayFromGit(new_finallist)
        val list = Delaunay.addHight(trianglelist!!, new_finallist)
        val list2 = Delaunay.doEdge(trianglelist!!, new_finallist)
        Log.d(TAG, "readNorthingEasting:list "+list)
        Log.d(TAG, "readNorthingEasting:list2 "+list2)
//        iterateTriangleList(trianglelist)
    }
    fun iterateTriangleList(triangleList: List<Triangle2D>?) {
        triangleList!!.forEach { triangle ->
            // Access properties of the Triangle2D object
            val aPoint = triangle.a
            val bPoint = triangle.b
            val cPoint = triangle.c
            println("aPoint == "+aPoint +"bPoint == "+bPoint+"cPoint == "+cPoint)
            println("Triangle with points: ${triangle.a}, ${triangle.b}, ${triangle.c}")
        }
    }

    /// In this part we read the facepoint from the .xml file
    fun readfacepoint(){
        for (j in ArraysT.indices) {
            if (ArraysT[j].contains("<F>")) {
                val Face_pointline = ArraysT[j]
                val face_index1 = Face_pointline.indexOf(">")
                for (k in face_index1 until Face_pointline.length) {
                    if (Face_pointline[k] == ' ') {
                        for (l in k + 1 until Face_pointline.length) {
                            if (Face_pointline[l] == ' ') {
                                for (m in l + 1 until Face_pointline.length) {
                                    if (Face_pointline[m] == '<') {
                                        newFace_temp_pointline.add(Face_pointline.substring(face_index1 + 1, k))
                                        newFace_temp_pointline.add(Face_pointline.substring(k, l))
                                        newFace_temp_pointline.add(Face_pointline.substring(l, m))
                                        newFace_pointline.add(newFace_temp_pointline.toString())
                                        newFace_temp_pointline.clear()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        newface_pointlines()
    }

    /// this method is used for getting the newface_pointlines from .xml file
    fun newface_pointlines() {
        if (newFace_pointline.size > 0) {
            facepoint_inCoordinate.clear()
            for (n in newFace_pointline.indices) {
                var data = newFace_pointline[n]
                data = data.replace("[", "")
                data = data.replace("]", "")
                val spilitdata = data.split(", ").toTypedArray()
                val i = spilitdata[0]
                val i1 = i.toInt()
                var j = spilitdata[1]
                j = j.replace(" ", "")
                val j1 = j.toInt()
                var k = spilitdata[2]
                k = k.replace(" ", "")
                val k1 = k.toInt()

                val temp_data = ArrayList<Double>()
                val temp_data1 = ArrayList<Double>()

                temp_data1.add(Northing[i1 - 1])
                temp_data1.add(Easting[i1 - 1])
                temp_data1.add(Elevation.get(i1 - 1))

                temp_data1.add(Northing[j1 - 1])
                temp_data1.add(Easting[j1 - 1])
                temp_data1.add(Elevation.get(j1 - 1))

                temp_data1.add(Northing[k1 - 1])
                temp_data1.add(Easting[k1 - 1])
                temp_data1.add(Elevation.get(k1 - 1))

                final_tempList.add(Northing[i1 - 1])
                final_tempList.add(Easting[i1 - 1])
                final_tempList.add(Elevation.get(i1 - 1))

                final_tempList.add(Northing[j1 - 1])
                final_tempList.add(Easting[j1 - 1])
                final_tempList.add(Elevation.get(j1 - 1))

                final_tempList.add(Northing[k1 - 1])
                final_tempList.add(Easting[k1 - 1])
                final_tempList.add(Elevation.get(k1 - 1))

                facepoint_inCoordinate.add(temp_data1)
            }
        }
//        println("final_tempList"+final_tempList)
//        finaltrianglepoint()
        drawtrianglespoint()
    }

    fun drawtrianglespoint(){
        Log.d(TAG, "drawtrianglespoint: facepoint_inCoordinate"+facepoint_inCoordinate.toString())
        for (i in facepoint_inCoordinate.indices) {
            val temp_ListX = ArrayList<Double>()
            val temp_ListY = ArrayList<Double>()
            val temp_ListZ = ArrayList<Double>()
            val temp_newList = facepoint_inCoordinate[i]

            temp_ListX.add(temp_newList[0])
            temp_ListX.add(temp_newList[3])
            temp_ListX.add(temp_newList[6])
            temp_ListY.add(temp_newList[1])
            temp_ListY.add(temp_newList[4])
            temp_ListY.add(temp_newList[7])
            temp_ListZ.add(temp_newList[2])
            temp_ListZ.add(temp_newList[5])
            temp_ListZ.add(temp_newList[8])

            Log.d("TAG", "temp_ListX:= "+temp_ListX + "==" +"temp_ListY:= "+temp_ListY+ "==" +"temp_ListZ:= "+temp_ListZ)
        }

    }

    fun finaltrianglepoint(){
       var i = 0
         while (i in final_tempList.indices) {
             val tempPoints = ArrayList<Double>()
             tempPoints.add(final_tempList[i+0])
             tempPoints.add(final_tempList[i+1])
             tempPoints.add(final_tempList[i+2])
             Log.d(TAG, "tempPoints:"+tempPoints)
             i = i + 3
         }
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        } else {
            // Permission already granted, perform your file operations here
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, perform your file operations here
            } else {
                // Permission denied
            }
        }
    }


/*
        override fun onResume() {
            super.onResume()
            glSurfaceView.onResume()
        }

        override fun onPause() {
            super.onPause()
            glSurfaceView.onPause()
        }
*/

/*    fun createPoints(): ArrayList<Point3D> {
        val points = ArrayList<Point3D>()
        // Add your points to the list
        points.add(Point3D(0.0f, 0.0f, 0.0f)) // Origin point
        points.add(Point3D(0.0f, 0.0f, 0.0f)) // Point in the first quadrant
        points.add(Point3D(-1.0f, -1.0f, -1.0f)) // Point in the third quadrant

        return points
    }*/


}
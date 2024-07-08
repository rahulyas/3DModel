package com.example.a3dmodel.myView;

import static android.content.ContentValues.TAG;
//import static com.example.a3dmodel.myView.firstActivity3D.list2;
import static com.example.a3dmodel.myView.NewActivity.list2;

import android.opengl.GLES20;
import android.util.Log;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.List;

public class Triangle {

    private int mProgram;//Custom rendering pipeline program id
    private int muMMatrixHandle;//Position, rotation transformation matrix reference
    private int muMVPMatrixHandle;//Total transformation matrix reference id
    private int maPositionHandle; //Vertex position attribute reference id
    private int maColorHandle; //Vertex color attribute reference id
    private int uLightHandle;//Light source attribute reference id
    private int uCameraHandle;//view attribute reference id

    private FloatBuffer mVertexBuffer;//Vertex coordinate data buffer
    private FloatBuffer mTexCoorBuffer;//texture coordinate data buffer
    private int vCount=0;
    public float xAngle=0;//The angle of rotation around the x-axis
    public float yAngle=0;//The angle of rotation around the x-axis

    //Add
    private int sTextureGrassHandle;//grass id

    public Triangle(MyView mv)
    {
        //Initialize vertex coordinates and shading data
        initVertexData(mv);
        //Initialize the shader
        initShader(mv);
    }
    private void initVertexData(MyView mv)
    {
        //Initialization of vertex coordinate data
        //List<Float> sourcelist = DataLoad.loadFromASC(Main2Activity.fliename,mv.getResources());
/*        List<Float> sourcelist = PointCloudSourcelist;
        List<Triangle2D> trianglelist = Triangle1.trianglelist;*/
        List<Float> list = list2;
        vCount=list.size()/3;
        float[] vertices =  new float[list.size()];
        for (int i = 0; i < list.size(); i++) {
            vertices[i] = list.get(i);

        }
/*        Log.d("TAG", "initVertexData: sourcelist"+sourcelist);
        Log.d("TAG", "initVertexData: trianglelist"+trianglelist);*/
        Log.d("TAG", "newlist"+list);
//        for (int i = 0; i < list.size()-2; i=i+3) {
//            vertices[i] = 6f*list.get(i)/ DataLoad.scaleX-4f;
//            vertices[i+1] = 6f*list.get(i+1)/DataLoad.scaleY-4f;
//            vertices[i+2] = 1.5f*list.get(i+2)/DataLoad.scaleZ;
//        }

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length*4);
        vbb.order(ByteOrder.nativeOrder());
        mVertexBuffer = vbb.asFloatBuffer();
        mVertexBuffer.put(vertices);
        mVertexBuffer.position(0);

        //Initialization of vertex texture coordinate data
        float texCoor[]=new float[list.size()*2];
        int j = 0;
        for (int i = 0; i < vertices.length; i=i+3) {
            texCoor[j] = (vertices[i]+6f)/12f;
            texCoor[j+1] = (vertices[i+1]+6f)/12f;
            j=j+2;
            Log.d(TAG, "texCoor:J= "+texCoor[j]);
            Log.d(TAG, "texCoor:J+1= "+texCoor[j+1]);
        }

        //Create vertex texture coordinate data buffer
        ByteBuffer cbb = ByteBuffer.allocateDirect(texCoor.length*4);
        cbb.order(ByteOrder.nativeOrder());//set byte order
        mTexCoorBuffer = cbb.asFloatBuffer();//Convert to Float type buffer
        mTexCoorBuffer.put(texCoor);//Put vertex shader data into the buffer
        mTexCoorBuffer.position(0);//set buffer start position

    }
    //Initialize the shader
    private void initShader(MyView mv)
    {
        //Load the script content of the vertex shader
        String mVertexShader = ShaderUtil.loadFromAssertsFile("vertex.sh", mv.getResources());
        //Load the script content of the fragment shader
        String mFragmentShader = ShaderUtil.loadFromAssertsFile("frag.sh", mv.getResources());
        //Create programs based on vertex shaders and fragment shaders
        mProgram = ShaderUtil.createProgram(mVertexShader, mFragmentShader);
        //Get the vertex position attribute reference id in the program
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        //Get the vertex color attribute reference id in the program
        maColorHandle= GLES20.glGetAttribLocation(mProgram, "aTexCoor");
        //Get the total transformation matrix reference id in the program
        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //Get the transformation matrix reference id
        muMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMMatrix");
        //Get the light source vector reference id
        uLightHandle = GLES20.glGetUniformLocation(mProgram, "uLightLocation");

        uCameraHandle = GLES20.glGetUniformLocation(mProgram, "uCamera");

        //grassland
        sTextureGrassHandle=GLES20.glGetUniformLocation(mProgram, "sTexture");
    }

    public void drawSelf(int texId)
    {
        MatrixState.rotate(xAngle, 1, 0, 0);//Rotate around the X axis
        MatrixState.rotate(yAngle, 0, 0, 1);//Rotate around the Y axis

        //Formulate and use a certain shader program
        GLES20.glUseProgram(mProgram);

        //Pass the light position into the shader program
        GLES20.glUniform3fv(uLightHandle,1,MatrixState.lightPositionFB);
        //Pass the camera position into the shader program
        GLES20.glUniform3fv(uCameraHandle,1,MatrixState.cameraFB);
        //pass value to total transformation matrix
        GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, MatrixState.getFinalMatrix(), 0);
        //Pass the position and rotation transformation matrix to the shader program
        GLES20.glUniformMatrix4fv(muMMatrixHandle, 1, false, MatrixState.getMMatrix(), 0);
        //Specify vertex position data for brushes
        GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 3*4, mVertexBuffer);
        //Specifies the vertex texture coordinate data for the brush
        GLES20.glVertexAttribPointer(maColorHandle, 2, GLES20.GL_FLOAT, false, 2*4, mTexCoorBuffer);
        //Allows arrays of vertex position data
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        GLES20.glEnableVertexAttribArray(maColorHandle);
        //bind texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texId);

        GLES20.glUniform1i(sTextureGrassHandle, 0);//Use texture number 0


        GLES20.glDrawArrays(GLES20.GL_TRIANGLES,0, vCount);
    }
}

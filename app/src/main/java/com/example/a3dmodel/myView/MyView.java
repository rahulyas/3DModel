package com.example.a3dmodel.myView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
public class MyView extends GLSurfaceView {
    Triangle tle;
    private final float TOUCH_SCALE_FACTOR = 180.0f/320;// angle scaling
    private float mPreviousY;//The Y coordinate of the last touch position
    private float mPreviousX;//The X coordinate of the last touch position
    float lightOffset=-6;//The offset of the position or direction of the light

    public void setLightOffset(float lightOffset) {
        this.lightOffset = lightOffset;
    }

    SceneRenderer mRenderer;

    private float sizeCoef = 1;



    private float mScaleFactor = 1.0f;
    private float mMaxScaleFactor = 50.0f; // Maximum zoom-in factor
    private float mMinScaleFactor = 0.5f; // Maximum zoom-out factor
    private float mPreviousDistance = 0.0f; // Initialize the previous distance

    ScaleGestureDetector mScaleDetector;

    public MyView(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
//        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mRenderer=new SceneRenderer();
        this.setRenderer(mRenderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    //Touch event callback method
/*
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;//Calculate the stylus Y displacement
                float dx = x - mPreviousX;//Calculate stylus X displacement
                tle.yAngle+= dx * TOUCH_SCALE_FACTOR;
                tle.xAngle += dy * TOUCH_SCALE_FACTOR;
        }
        mPreviousY = y;//Record stylus position
        mPreviousX = x;//Record stylus position
        return true;
    }
*/
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY; // Calculate the stylus Y displacement
                float dx = x - mPreviousX; // Calculate stylus X displacement

                if (e.getPointerCount() >= 2) {
                    // Calculate the new distance between two fingers
                    float newDistance = calculateDistance(e);

                    // Calculate the zoom factor based on the distance change
                    float zoomFactor = newDistance / mPreviousDistance;

                    // Apply the zoom factor and limit within specified range
                    mScaleFactor *= zoomFactor;
                    mScaleFactor = Math.max(mMinScaleFactor, Math.min(mMaxScaleFactor, mScaleFactor));

                    // Update the camera frustum
                    updateCameraFrustum();

                    requestRender();

                    mPreviousDistance = newDistance;

                    // Zooming
                    float dz = dy - dx; // Calculate the net movement for zooming

                    if (dz > 0) {
                        mScaleFactor *= 0.9f; // Zoom out
                    } else if (dz < 0) {
                        mScaleFactor *= 1.1f; // Zoom in
                    }
                    // Limit zooming within specified range
                    mScaleFactor = Math.max(mMinScaleFactor, Math.min(mMaxScaleFactor, mScaleFactor));

                    // Update the camera frustum
                    updateCameraFrustum();
                    requestRender();

                } else {
                    // Rotation
                    tle.yAngle += dx * TOUCH_SCALE_FACTOR;
                    tle.xAngle += dy * TOUCH_SCALE_FACTOR;
                    // Update the camera frustum
                    updateCameraFrustum();
                    requestRender();
                }
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (e.getPointerCount() >= 2) {
                    // Calculate the initial distance between two fingers
                    mPreviousDistance = calculateDistance(e);
                }
                break;
        }

        mPreviousY = y; // Record stylus position
        mPreviousX = x; // Record stylus position
        return true;
    }

    private float calculateDistance(MotionEvent event) {
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    private class SceneRenderer implements Renderer{

        //The texture id of the mountain
        int mountionId;
        //int plainid;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //Set the screen background color RGBA
            GLES20.glClearColor(0,0,0,1.0f);
            //Create a triangle pair object
            tle=new Triangle(MyView.this);
            //Turn on deep detection
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            //Initialize the texture
            try {
                mountionId = initTexture("big3.png");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Turn off back clipping
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
             //Set window size and position
            GLES20.glViewport(0, 0, width, height);
            updateCameraFrustum();
/*            //Calculate aspect ratio of GLSurfaceView
            float ratio = (float) width / height;
            //Call this method to calculate the perspective projection matrix
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 5);*/
            //Call this method to generate a camera 9-parameter position matrix
            MatrixState.setCamera(0, 0f,  Activity3D.cameraSet,  0f, 0f, 0f,  0f, 1.0f, 0.0f);

            MatrixState.setInitMatrix();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
             //Clear depth buffer and color buffer
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //Initialize the position of the light source
            MatrixState.setLightLocation(0f, 2f, 7f);

//            gl.glScalef(sizeCoef, sizeCoef, 0);  // if You have a 3d object put sizeCoef as all parameters

            //protect the scene
            MatrixState.pushMatrix();

            //draw triangle pairs
            MatrixState.pushMatrix();
            tle.drawSelf(mountionId);
            MatrixState.popMatrix();

            //recovery site
            MatrixState.popMatrix();
        }
    }

    public int initTexture(String filename) throws IOException//textureId
    {
        //Generate Texture ID
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);  //Number of generated texture ids Array of texture ids Offset
        int textureId=textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER,GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,GLES20.GL_REPEAT);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,GLES20.GL_REPEAT);
        //Load image through input stream===============begin===================
        InputStream is = this.getResources().getAssets().open(filename);
        Bitmap bitmapTmp = BitmapFactory.decodeStream(is);
        is.close();
        //actually load the texture
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTmp, 0 );  //Texture type, must be GL10.GL_TEXTURE_2D in OpenGL ES

        //Automatically generate mipmap textures
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D);

        bitmapTmp.recycle(); 		  //Release the image after the texture is loaded successfully
        return textureId;
    }

    private void updateCameraFrustum() {
        float ratio = getWidth() / (float) getHeight();
        float left = -ratio * mScaleFactor;
        float right = ratio * mScaleFactor;
        float bottom = -mScaleFactor;
        float top = mScaleFactor;

        // Call this method to calculate the updated perspective projection matrix
        MatrixState.setProjectFrustum(left, right, bottom, top, 1, 15);
    }

/*
    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        float scaleFocusX = 0;
        float scaleFocusY = 0;
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector scaleGestureDetector) {
            float scale = scaleGestureDetector.getScaleFactor() * sizeCoef;

            sizeCoef = -scale;

            requestRender();

            return true;
        }

        @Override
        public boolean onScaleBegin(@NonNull ScaleGestureDetector scaleGestureDetector) {
            invalidate();

            scaleFocusX = scaleGestureDetector.getFocusX();
            scaleFocusY = scaleGestureDetector.getFocusY();

            return true;
        }

        @Override
        public void onScaleEnd(@NonNull ScaleGestureDetector scaleGestureDetector) {
            scaleFocusX = 0;
            scaleFocusY = 0;
        }
    }
*/
}

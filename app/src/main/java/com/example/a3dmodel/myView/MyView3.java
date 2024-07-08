package com.example.a3dmodel.myView;

import static androidx.constraintlayout.widget.Constraints.TAG;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MyView3 extends GLSurfaceView {

    SceneRenderer3 mRenderer;
    Triangle1 triangle1;

    private final float TOUCH_SCALE_FACTOR = 180.0f/320;//Angle scaling
    private float mPreviousY;//The Y coordinate of the last touch position
    private float mPreviousX;//The X coordinate of the last touch position


    private float mScaleFactor = 1.0f;
    private float mMaxScaleFactor = 50.0f; // Maximum zoom-in factor
    private float mMinScaleFactor = 0.5f; // Maximum zoom-out factor

    static final int NONE = 0;
    static final int DRAG = 1;
    static final int ZOOM = 2;
    static final int CLICK = 3;
    int mode = NONE;
    private float mPreviousDistance = 0.0f; // Initialize the previous distance

    public MyView3(Context context) {
        super(context);
        this.setEGLContextClientVersion(2);
        mRenderer = new SceneRenderer3();
        this.setRenderer(mRenderer);
        this.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public MyView3(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //Touch event callback method
 /*   @Override

    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY;//Calculate the stylus Y displacement
                float dx = x - mPreviousX;//Calculate stylus X displacement
                triangle1.yAngle+= dx * TOUCH_SCALE_FACTOR;
                triangle1.xAngle += dy * TOUCH_SCALE_FACTOR;
        }
        mPreviousY = y;//Record stylus position
        mPreviousX = x;//Record stylus position
        return true;
    }
*/
/*    @Override
    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY; // Calculate the stylus Y displacement
                float dx = x - mPreviousX; // Calculate stylus X displacement

                triangle1.yAngle += dx * TOUCH_SCALE_FACTOR;
                triangle1.xAngle += dy * TOUCH_SCALE_FACTOR;

*//*                // Zooming logic
                if (dy > 0) {
                    mScaleFactor *= 1.1f; // Zoom in
                } else {
                    mScaleFactor *= 0.9f; // Zoom out
                }*//*
                // Zooming
                float dz = dy - dx; // Calculate the net movement for zooming

                if (dz > 0) {
                    mScaleFactor *= 1.1f; // Zoom in
                } else if (dz < 0) {
                    mScaleFactor *= 0.9f; // Zoom out
                }
                // Limit zooming within specified range
                mScaleFactor = Math.max(mMinScaleFactor, Math.min(mMaxScaleFactor, mScaleFactor));

                // Update the camera frustum
                updateCameraFrustum();

                requestRender();
                break;

            //first finger is lifted
            case MotionEvent.ACTION_UP:
                mode = NONE;
                int xDiff = (int) (x - mPreviousX);
                int yDiff = (int) (y - mPreviousY);
                if (xDiff < CLICK && yDiff < CLICK)
                    performClick();
                break;
            // second finger is lifted
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;

        }

        mPreviousY = y; // Record stylus position
        mPreviousX = x; // Record stylus position
        return true;
    }*/

    public boolean onTouchEvent(MotionEvent e) {
        float y = e.getY();
        float x = e.getX();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousY; // Calculate the stylus Y displacement
                float dx = x - mPreviousX; // Calculate stylus X displacement

                if (e.getPointerCount() >= 2) {
                    // Calculate the initial distance between two fingers
                    float newDistance = calculateDistance(e);

                    // Calculate the change in distance for zooming
                    float dz = newDistance - mPreviousDistance;

                    // Zooming
                    mScaleFactor *= 1.0f + dz * 0.01f; // Adjust the zoom factor based on dz

                    // Limit zooming within specified range
                    mScaleFactor = Math.max(mMinScaleFactor, Math.min(mMaxScaleFactor, mScaleFactor));

/*
                        // Calculate the new distance between two fingers
                    float newDistance = calculateDistance(e);

                    // Calculate the zoom factor based on the distance change
                    float zoomFactor = newDistance / mPreviousDistance;

                    // Apply the zoom factor and limit within specified range
                    mScaleFactor *= zoomFactor;
                    mScaleFactor = Math.max(mMinScaleFactor, Math.min(mMaxScaleFactor, mScaleFactor));
*/

                    // Update the camera frustum
                    updateCameraFrustum();

                    requestRender();

                    mPreviousDistance = newDistance;
/*                    // Zooming
                    float dz = dy - dx; // Calculate the net movement for zooming

                    if (dz > 0) {
                        mScaleFactor *= 1.1f; // Zoom in
                    } else if (dz < 0) {
                        mScaleFactor *= 0.9f; // Zoom out
                    }
                    // Limit zooming within specified range
                    mScaleFactor = Math.max(mMinScaleFactor, Math.min(mMaxScaleFactor, mScaleFactor));

                    // Update the camera frustum
                    updateCameraFrustum();
                    requestRender();*/

                } else {
                    // Rotation
                    triangle1.yAngle += dx * TOUCH_SCALE_FACTOR;
                    triangle1.xAngle += dy * TOUCH_SCALE_FACTOR;
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

            case MotionEvent.ACTION_POINTER_UP:
                if (e.getPointerCount() >= 2) {
                    // Reset previous distance
                    mPreviousDistance = -1;
                }
                break;

        }

        mPreviousY = y; // Record stylus position
        mPreviousX = x; // Record stylus position
        return true;
    }

/*    private float calculateDistance(MotionEvent event) {
        float dx = event.getX(0) - event.getX(1);
        float dy = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(dx * dx + dy * dy);
    }*/

    private float calculateDistance(MotionEvent e) {
        float x1 = e.getX(0);
        float y1 = e.getY(0);
        float x2 = e.getX(1);
        float y2 = e.getY(1);
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private class SceneRenderer3 implements GLSurfaceView.Renderer{

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            //Set the screen background color RGBA
            GLES20.glClearColor(0f,0f,0f,1.0f);
            triangle1 = new Triangle1(MyView3.this);
            //Turn on deep detection
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            //Set window size and position
            GLES20.glViewport(0, 0, width, height);
            updateCameraFrustum();
/*            //Calculate aspect ratio of GLSurfaceView
            float ratio = (float) width / height;
            Log.d(TAG, "onSurfaceChanged: width & height == "+ratio);
            //Call this method to calculate the perspective projection matrix
            MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 1, 15);*/
            // Call this method to generate camera 9 parameter position matrix
            MatrixState.setCamera(0, 0f, Activity3D.cameraSet,  0f, 0f, 0f,  0f, 1.0f, 0.0f);

            MatrixState.setInitMatrix();
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            //Clear depth buffer and color buffer
            GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            //protect the scene
            MatrixState.pushMatrix();

            //draw triangle pairs
            MatrixState.pushMatrix();
            triangle1.drawSelf();
            MatrixState.popMatrix();

            //recovery site
            MatrixState.popMatrix();
        }
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

}

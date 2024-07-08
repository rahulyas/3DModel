package com.example.a3dmodel.myView;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.example.a3dmodel.R;
import com.example.a3dmodel.Utils;
import com.example.a3dmodel.arithmetic.Delaunay;
import com.example.a3dmodel.pointtoland;

import java.util.ArrayList;
import java.util.List;

import io.github.jdiemke.triangulation.Triangle2D;

public class Activity3D extends AppCompatActivity {
//    public static float cameraSet = 7.9f;
    public static float cameraSet = 2.9f;
    MyView3 myView;
    PopupWindow popupWindow;
    private WaitScreen waitScreen;
    public static boolean hasloaded = false;
    public static int time = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        myView=new MyView3(this);

        if (hasloaded) {
            Activity3D.time = 2;
        }
        new Handler().postDelayed(() -> {
            if (popupWindow != null) {
                waitScreen.close();
            }
        }, Activity3D.time * 2000);
        hasloaded = true;
        setContentView(R.layout.activity_main2);

        LinearLayout ll = (LinearLayout) findViewById(R.id.activity_main3);
        ll.addView(myView);

        Button third = findViewById(R.id.thirdbutton);
        third.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Activity3D.this,SecondActivity3D.class);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onAttachedToWindow() {
        show();
        super.onAttachedToWindow();
    }

    private void show() {
        waitScreen=new WaitScreen(this);
        popupWindow=waitScreen.show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        myView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myView.onPause();
    }


}
package com.example.a3dmodel.myView;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.a3dmodel.R;

public class SecondActivity3D extends AppCompatActivity {
    MyView myView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView=new MyView(this);
        setContentView(R.layout.activity_second_activity3_d);
        LinearLayout ll = (LinearLayout) findViewById(R.id.activity_main);
        ll.addView(myView);
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
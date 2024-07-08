package com.example.a3dmodel.myView;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import static com.example.a3dmodel.myView.firstActivity3D.finalfacepointlist;
import static com.example.a3dmodel.myView.firstActivity3D.new_finallist;
import static com.example.a3dmodel.pointtoland.facepointslist1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.a3dmodel.R;

import java.util.ArrayList;
import java.util.List;

import io.github.jdiemke.triangulation.Triangle2D;

public class NewActivity extends AppCompatActivity {
    static List<Triangle2D> triangleList = null;
    static List<Float> list1 = null;
    static List<Float> list2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        Button button3 = (Button) findViewById(R.id.drawsecond);
        button3.setOnClickListener(view -> {
            Intent intent = new Intent(NewActivity.this,Activity3D.class);
            startActivity(intent);
        });

/*        triangleList = Delaunay1.doDelaunayFromGit(new_finallist);
        list1 = Delaunay1.doEdge(triangleList,new_finallist);
        list2 = Delaunay1.addHight(triangleList,new_finallist);

        Log.d(TAG, "onActivityResult: new_finallist == "+new_finallist);
        Log.d(TAG, "onActivityResult: triangleList == "+triangleList);
        Log.d(TAG, "onActivityResult: list1 == "+list1);
        Log.d(TAG, "onActivityResult: list2 == "+list2);*/
        ArrayList<Double> doubleList = new ArrayList<>();
        doubleList =  finalfacepointlist;
//        doubleList =  facepointslist1;
        Log.d(TAG, "doubleList == "+doubleList);
        Log.d(TAG, "finalfacepointlist == "+facepointslist1);

        // Convert Double ArrayList to List<Float>
        List<Float> floatList = new ArrayList<>();
        for (Double value : doubleList) {
            floatList.add(value.floatValue());
        }

        list1 = floatList;
        list2 = floatList;
        Log.d(TAG, "onActivityResult: list1 == "+floatList);

    }
}
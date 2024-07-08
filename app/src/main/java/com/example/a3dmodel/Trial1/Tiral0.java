package com.example.a3dmodel.Trial1;

import static androidx.constraintlayout.widget.Constraints.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.example.a3dmodel.R;
import com.example.a3dmodel.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

public class Tiral0 extends AppCompatActivity {
    Button button2;
    private final int PICK_TEXT = 101;
    Uri fileuri;
    List<String> list = new ArrayList<>();

    ArrayList<Float> Northing = new ArrayList<>();
    ArrayList<Float> Easting = new ArrayList<>();
    ArrayList<Float> Elevation = new ArrayList<>();

    static List<Float> finalfacepointlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tiral0);
        button2 = findViewById(R.id.button1);
        button2.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/*");
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_TEXT);
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_TEXT && data != null) {
            fileuri = data.getData();
            list = new Utils().readAnyfile(fileuri, Tiral0.this);
            NewSplitDataLandXml(list);
        }
//        Log.d(TAG, "onActivityResult: list == "+list);
    }

    public void NewSplitDataLandXml(List<String> list) {
        List<Double> firstlist = new ArrayList<>();
        ArrayList<Integer> temp_facepoint = new ArrayList<>();
        for (String item : list) {
            String[] splitData = item.split(", ");
//            Log.d(TAG, "readText:splitData ==" + Arrays.toString(splitData));
            for (String input : splitData) {
                // Find the start and end indexes of <P> tag content
                int startIdx = input.indexOf(">");
//                int endIdx = input.lastIndexOf("<");
                int endIdx = input.lastIndexOf("</P");
                // Check if <P> tag content was found
                if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
                    String content = input.substring(startIdx + 1, endIdx);
                    // Split the content into an array of strings using space as the delimiter
                    String[] values = content.split(" ");
                    double easting = Double.parseDouble(values[0]);
                    double northing = Double.parseDouble(values[1]);
                    double elevation = Double.parseDouble(values[2]);
                    Northing.add((float) northing);
                    Easting.add((float) easting);
                    Elevation.add((float) elevation);
/*                    firstlist.add(easting);
                    firstlist.add(northing);
                    firstlist.add(elevation);*/
//                    System.out.println("Easting ="+Easting +"=Northing="+ Northing +"=Elevation="+Elevation);
                } else {
                    System.out.println("Invalid input format: ");
                }

                int FaceendIdx = input.lastIndexOf("</F>");
                if (startIdx != -1 && FaceendIdx != -1 && startIdx < FaceendIdx) {
                    String content = input.substring(startIdx + 1, FaceendIdx);
                    // Split the content into an array of strings using space as the delimiter
                    String[] values = content.split(" ");
                    int P1 = Integer.parseInt(values[0]);
                    int P2 = Integer.parseInt(values[1]);
                    int P3 = Integer.parseInt(values[2]);
                    temp_facepoint.add(P1);
                    temp_facepoint.add(P2);
                    temp_facepoint.add(P3);
//                    System.out.println("P1 ="+P1 +"=P2="+ P2 +"=P3="+P3);
                } else {
//                    System.out.println("Invalid input format: ");
                }

            }
        }

        float northingmax = Collections.max(Northing);
        float northingmin = Collections.min(Northing);

        float eastingmax = Collections.max(Easting);
        float eastingmin = Collections.min(Easting);

        float elevationmax = Collections.max(Elevation);
        float elevationmin = Collections.min(Elevation);

        float meannorthing = (northingmin+northingmax)/2;
        float meaneasting = (eastingmax+eastingmin)/2;
        float meanelevation = (elevationmax+elevationmin)/2;

        Easting.replaceAll(number -> (number - meaneasting)/100);
        Northing.replaceAll(number -> (number - meannorthing)/100);
        Elevation.replaceAll(number -> (number - meanelevation)/100);

/*        for(int k=0 ;k< temp_facepoint.size(); k++){
            int value = temp_facepoint.get(k);
            finalfacepointlist.add(Easting.get(value-1));
            finalfacepointlist.add(Northing.get(value-1));
            finalfacepointlist.add(Elevation.get(value-1));
        }*/
        IntStream.range(0, temp_facepoint.size())
                .mapToObj(k -> {
                    int value = temp_facepoint.get(k);
                    return Arrays.asList(
                            Easting.get(value - 1),
                            Northing.get(value - 1),
                            Elevation.get(value - 1)
                    );
                })
                .forEach(finalfacepointlist::addAll);

//        Log.d(TAG, "NewSplitDataLandXml: =="+firstlist);
//        Log.d(TAG, "NewSplitDataLandXmlfacepoint: =="+temp_facepoint);
        Log.d(TAG, "finalfacepointlist: =="+finalfacepointlist);
    }
}
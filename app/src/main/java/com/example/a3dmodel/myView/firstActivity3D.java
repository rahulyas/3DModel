package com.example.a3dmodel.myView;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;

import com.example.a3dmodel.R;
import com.example.a3dmodel.Trial1.Tiral0;
import com.example.a3dmodel.Utils;
import com.example.a3dmodel.pointtoland;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import io.github.jdiemke.triangulation.Triangle2D;

public class firstActivity3D extends AppCompatActivity {
    private final int PICK_TEXT = 101;
    static List<Float> new_finallist = new ArrayList<>();
    static List<Float> finalnew_finallist = new ArrayList<>();
    List<String> list = new ArrayList<>();
    Uri fileuri;
/*    static List<Triangle2D> triangleList = null;
    static List<Float> list1 = null;
    static List<Float> list2 = null;*/

/*    ArrayList<Float> FirstNorthing = new ArrayList<>();
    ArrayList<Float> FirstEasting = new ArrayList<>();
    ArrayList<Float> FirstElevation = new ArrayList<>();*/

    ArrayList<Double> FirstNorthing = new ArrayList<>();
    ArrayList<Double> FirstEasting = new ArrayList<>();
    ArrayList<Double> FirstElevation = new ArrayList<>();


    ArrayList<Double> Northing = new ArrayList<>();
    ArrayList<Double> Easting = new ArrayList<>();
    ArrayList<Double> Elevation = new ArrayList<>();

    String[] ArraysT;
    ArrayList<String> newFace_pointline = new ArrayList<>();
    ArrayList<String> newFace_temp_pointline = new ArrayList<>();

    static ArrayList<Double> finalfacepointlist = new ArrayList<>();
    private static final int PERMISSION_REQUEST_STORAGE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_activity3_d);
        onRequestpermission();
        Button button2 = (Button) findViewById(R.id.button1);
        button2.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("text/*");
            startActivityForResult(Intent.createChooser(intent, "Select File"), PICK_TEXT);
        });

        Button button3 = (Button) findViewById(R.id.button2);
        button3.setOnClickListener(view -> {
//            Intent intent = new Intent(firstActivity3D.this,Activity3D.class);
//            newface_pointlines();
            Intent intent = new Intent(firstActivity3D.this,NewActivity.class);
            startActivity(intent);
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_TEXT && data != null) {
            fileuri = data.getData();
//            readText(getFilePath(fileuri));
            list = new Utils().readAnyfile(fileuri, firstActivity3D.this);
            NewSplitDataLandXml(list);

/*            String path = new Utils().getFilePath(fileuri, firstActivity3D.this);
            String[] splitData = path.split("\\.");
            String splitDataPath = splitData[1];
            if (splitData.length > 0) {
                if (splitDataPath.contains("xml")) {
*//*                    new Utils().readText(new Utils().getFilePath(fileuri),firstActivity3D.this);
                    ArrayList<Double> doubleList = new ArrayList<>();
                    doubleList = new Utils().readNorthingEasting();
                    for (Double doubleValue : doubleList) {
                        new_finallist.add(doubleValue.floatValue());
                    }*//*
                list = new Utils().readAnyfile(fileuri, firstActivity3D.this);
                NewSplitDataLandXml(list);
//                    new_finallist = new Utils().newface_pointlines();
                    System.out.println("onActivityResult: new_finallist"+new_finallist);
//                    Log.d(TAG, "onActivityResult: new_finallist"+new_finallist);
                    System.out.println("onActivityResult: new_finallist"+new_finallist);
                    int i = 0;
                    int id = 1;
                    while (i < new_finallist.size()) {
                        FirstNorthing.add(new_finallist.get(i + 0));
                        FirstEasting.add(new_finallist.get(i + 1));
                        FirstElevation.add(new_finallist.get(i + 2));
                        i = i + 3;
                    }

                    System.out.println("onActivityResult: FirstNorthing"+FirstNorthing);
                    System.out.println("onActivityResult: FirstEasting"+FirstEasting);
                    System.out.println("onActivityResult: FirstElevation"+FirstElevation);

                    float northingmax = Collections.max(FirstNorthing);
                    float northingmin = Collections.min(FirstNorthing);

                    float eastingmax = Collections.max(FirstEasting);
                    float eastingmin = Collections.min(FirstEasting);

                    float elevationmax = Collections.max(FirstElevation);
                    float elevationmin = Collections.min(FirstElevation);

                    float meannorthing = (northingmin+northingmax)/2;
                    float meaneasting = (eastingmax+eastingmin)/2;
                    float meanelevation = (elevationmax+elevationmin)/2;

                    System.out.println("onActivityResult: meannorthing"+meannorthing);
                    System.out.println("onActivityResult: meaneasting"+meaneasting);
                    System.out.println("onActivityResult: meanelevation"+meanelevation);

//                    FirstNorthing.forEach(number -> System.out.println(number - meannorthing));
//                    FirstEasting.forEach(number -> System.out.println(number - meaneasting));
//                    FirstElevation.forEach(number -> System.out.println(number - meanelevation));


                    FirstNorthing.replaceAll(number -> (number - meannorthing)/100);
                    FirstEasting.replaceAll(number -> (number - meaneasting)/100);
                    FirstElevation.replaceAll(number -> (number - meanelevation)/100);
//


                    System.out.println("onActivityResult: FirstNorthing"+FirstNorthing.size()+"=="+FirstEasting.size()+"=="+FirstElevation.size());

                    System.out.println("onActivityResult: FirstNorthing"+FirstNorthing);
                    System.out.println("onActivityResult: FirstEasting"+FirstEasting);
                    System.out.println("onActivityResult: FirstElevation"+FirstElevation);
                    new_finallist.clear();
                    for(int j=0 ; j< FirstNorthing.size();j++){
//                        finalnew_finallist.add(FirstNorthing.get(j));
//                        finalnew_finallist.add(FirstEasting.get(j));
//                        finalnew_finallist.add(FirstElevation.get(j));
                        new_finallist.add(FirstNorthing.get(j));
                        new_finallist.add(FirstEasting.get(j));
                        new_finallist.add(FirstElevation.get(j));
                    }

                    System.out.println("onActivityResult: new_finallist"+new_finallist);

                } else if (splitDataPath.contains("txt")) {
                    list = new Utils().readAnyfile(fileuri,firstActivity3D.this);
                    // Convert ArrayList<Double> to List<Float>
                    ArrayList<Double> doubleList = new ArrayList<>();
                    doubleList = new Utils().SplitDataList(list);
                    for (Double doubleValue : doubleList) {
                        new_finallist.add(doubleValue.floatValue());
                    }
                }
            }*/
/*            triangleList = Delaunay1.doDelaunayFromGit(new_finallist);
            list1 = Delaunay1.doEdge(triangleList,new_finallist);
            list2 = Delaunay1.addHight(triangleList,new_finallist);
            Log.d(TAG, "onActivityResult: new_finallist == "+new_finallist);
            Log.d(TAG, "onActivityResult: triangleList == "+triangleList);
            Log.d(TAG, "onActivityResult: list1 == "+list1);
            Log.d(TAG, "onActivityResult: list2 == "+list2);*/

        }
    }
    public void NewSplitDataLandXml(List<String> list) {
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
                    Easting.add(easting);
                    Northing.add(northing);
                    Elevation.add(elevation);
//                    new_finallist.add((float)easting);
//                    new_finallist.add((float)northing);
//                    new_finallist.add((float)elevation);
//                    firstlist.add(easting);
//                    firstlist.add(northing);
//                    firstlist.add(elevation);

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

        System.out.println("BeforeEasting ="+Easting+"=BeforeNorthing="+ Northing.size() +"=BeforeElevation="+Elevation.size());

        double eastingmax = Collections.max(Easting);
        double eastingmin = Collections.min(Easting);

        double northingmax = Collections.max(Northing);
        double northingmin = Collections.min(Northing);

        double elevationmax = Collections.max(Elevation);
        double elevationmin = Collections.min(Elevation);

        double meaneasting = (eastingmax+eastingmin)/2;
        System.out.println("meaneasting ="+meaneasting);
        double meannorthing = (northingmin+northingmax)/2;
        double meanelevation = (elevationmax+elevationmin)/2;

        Easting.replaceAll(number -> (number - meaneasting)/100);
        Northing.replaceAll(number -> (number - meannorthing)/100);
        Elevation.replaceAll(number -> (number - meanelevation)/100);


        for(int j=0;j< Easting.size();j++){
            double calcuatednorthing = (Northing.get(j)-meannorthing)/100;
            double calcuatedeasting = (Easting.get(j)-meaneasting)/100;
            double calcuatedelevation = (Elevation.get(j)-meanelevation)/100;
            FirstNorthing.add(calcuatednorthing);
            FirstEasting.add(calcuatedeasting);
            FirstElevation.add(calcuatedelevation);
        }

        System.out.println("AfterEasting ="+FirstEasting +"=AfterNorthing="+ FirstNorthing.size() +"=AfterElevation="+FirstElevation.size());
        for(int k=0 ;k< temp_facepoint.size(); k++){
            int value = temp_facepoint.get(k);
//            System.out.println("value == "+value);
            finalfacepointlist.add(FirstEasting.get(value-1));
            finalfacepointlist.add(FirstNorthing.get(value-1));
            finalfacepointlist.add(FirstElevation.get(value-1));
        }
        IntStream.range(0, temp_facepoint.size())
                .mapToObj(k -> {
                    int value = temp_facepoint.get(k);
                    Log.d(TAG, "NewSplitDataLandXml: value =="+value);
                    return Arrays.asList(
                            Easting.get(value - 1),
                            Northing.get(value - 1),
                            Elevation.get(value - 1)
                    );
                })
                .forEach(finalfacepointlist::addAll);



//        Log.d(TAG, "NewSplitDataLandXml: =="+firstlist);
        Log.d(TAG, "NewSplitDataLandXmlfacepoint: =="+temp_facepoint.size()/3);
        Log.d(Constraints.TAG, "finalfacepointlist: =="+finalfacepointlist);
        Log.d(Constraints.TAG, "finalfacepointlistsize: =="+finalfacepointlist.size()/9);

        }

    /**********************************************************************************/


    public String getFilePath(Uri uri) {
        String[] filename1;
        String fn;
        String filepath = uri.getPath();
        String filePath1[] = filepath.split(":");
        filename1 = filepath.split("/");
        fn = filename1[filename1.length - 1];
        return Environment.getExternalStorageDirectory().getPath() + "/" + filePath1[1];
    }



    /// reading the Text file
    public String readText(String input) {
        File file = new File(input);
        byte[] bytes = new byte[(int) file.length()];
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append("\n");
                ArraysT = line.split("\n");
                readNorthingEasting();
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return text.toString();
    }

    ////////////////////////////////////////////////////////////////
    public void readNorthingEasting() {

        for (int i = 0; i < ArraysT.length; i++) {
            if (ArraysT[i].contains("<P id=")) {
                int index = 0;
                int index2 = 0;
                int st_index = 0;
                int index3 = 0;
                String point_line = ArraysT[i];
                st_index = point_line.indexOf("\">");
//                            Log.d("point_line", String.valueOf(point_line));
                for (index = st_index; index < point_line.length(); index++) {

                    if (point_line.charAt(index) == ' ') {
                        Log.d("index", String.valueOf(index));
                        for (index2 = index + 2; index2 < point_line.length(); index2++) {
                            if (point_line.charAt(index2) == ' ') {
                                Log.d("index2", String.valueOf(index2));
                                for(index3 = index2 + 3 ; index3<point_line.length();index3++){
                                    if (point_line.charAt(index3) =='<') {
                                        Easting.add(Double.valueOf(point_line.substring(index + 1, index2 + 1)));
                                        Northing.add(Double.valueOf(point_line.substring(st_index + 2, index + 1)));
                                        Elevation.add(Double.valueOf(point_line.substring(index2+1, index3)));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        readthefacepoint();
//        Log.d("arrayListZ", ""+arrayListZ);
    }
    ////////////////////////////////////////////////////////////////
    public void readthefacepoint() {
        for (int j = 0; j < ArraysT.length; j++) {
            if (ArraysT[j].contains("<F>")) {
                String Face_pointline = ArraysT[j];
                int face_index1 = Face_pointline.indexOf(">");
                for (int k = face_index1; k < Face_pointline.length(); k++) {
                    if (Face_pointline.charAt(k) == ' ') {
                        int face_index2 = k;
                        for (int l = face_index2 + 1; l < Face_pointline.length(); l++) {
                            if (Face_pointline.charAt(l) == ' ') {
                                int face_index3 = l;
                                for (int m = face_index3 + 1; m < Face_pointline.length(); m++) {
                                    if (Face_pointline.charAt(m) == '<') {
                                        newFace_temp_pointline.add(Face_pointline.substring(face_index1 + 1, k));
                                        newFace_temp_pointline.add(Face_pointline.substring(face_index2, l));
                                        newFace_temp_pointline.add(Face_pointline.substring(face_index3, m));
                                        newFace_pointline.add(String.valueOf(newFace_temp_pointline));
                                        newFace_temp_pointline.clear();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void newface_pointlines() {
        double eastingmax = Collections.max(Easting);
        double eastingmin = Collections.min(Easting);

        double northingmax = Collections.max(Northing);
        double northingmin = Collections.min(Northing);

        double elevationmax = Collections.max(Elevation);
        double elevationmin = Collections.min(Elevation);

        double meaneasting = (eastingmax+eastingmin)/2;
        double meannorthing = (northingmin+northingmax)/2;
        double meanelevation = (elevationmax+elevationmin)/2;

        Easting.replaceAll(number -> (number - meaneasting)/100);
        Northing.replaceAll(number -> (number - meannorthing)/100);
        Elevation.replaceAll(number -> (number - meanelevation)/100);

        System.out.println("AfterEasting ="+Easting.get(2) +"=AfterNorthing="+ Northing.size() +"=AfterElevation="+Elevation.size());

        Log.d(TAG, "face_size: " + newFace_pointline.size());
        if (newFace_pointline.size() > 0) {
            for (int n = 0; n < newFace_pointline.size(); n++) {
                String data = newFace_pointline.get(n);
                data = data.replace("[", "");
                data = data.replace("]", "");
                String[] spilitdata = data.split(", ");
                String i = spilitdata[0];
                int i1 = Integer.parseInt(i);
                String j = spilitdata[1];
                j = j.replace(" ", "");
                int j1 = Integer.parseInt(j);
                String k = spilitdata[2];
                k = k.replace(" ", "");
                int k1 = Integer.parseInt(k);
                ArrayList<Double> temp_data = new ArrayList<Double>();

                finalfacepointlist.add(Easting.get(i1-1));
                finalfacepointlist.add(Northing.get(i1-1));
                finalfacepointlist.add(Elevation.get(i1-1));
                finalfacepointlist.add(Easting.get(j1-1));
                finalfacepointlist.add(Northing.get(j1-1));
                finalfacepointlist.add(Elevation.get(j1-1));
                finalfacepointlist.add(Easting.get(k1-1));
                finalfacepointlist.add(Northing.get(k1-1));
                finalfacepointlist.add(Elevation.get(k1-1));


            }
        }
        Log.d(Constraints.TAG, "finalfacepointlist: =="+finalfacepointlist);
    }
    public void onRequestpermission(){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE} , PERMISSION_REQUEST_STORAGE);
        }
    }
}
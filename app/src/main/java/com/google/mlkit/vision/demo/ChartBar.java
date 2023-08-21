package com.google.mlkit.vision.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import org.jetbrains.annotations.NotNull;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.github.mikephil.charting.charts.BarChart;

public class ChartBar extends AppCompatActivity {

  private static final String TAG = "ChartBar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);

        Intent i=getIntent();
        String j =i.getStringExtra("Date");
        Log.d(TAG,""+j.toString()+"");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference d= db.collection("users").document(user.getUid()).collection("dates").document(j.toString());
        Map<String,Long> required_data =new HashMap<String,Long>();


        d.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            drawbarchart(documentSnapshot);

                        }
                }}).

            addOnFailureListener(new OnFailureListener(){
                @Override
                public void onFailure(Exception e){

                }
            });

    }
    public void drawbarchart(DocumentSnapshot documentSnapshot){
        BarChart barChart = findViewById(R.id.barChart);
        List<BarEntry> entries = new ArrayList<>();
        List<String> exerciseNames = new ArrayList<>();
        List<Integer> colors = new ArrayList<>(); // List to store colors
        Map<String, Object> dataMap = documentSnapshot.getData();
        int index = 0;
        // Print the extracted data
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            if (entry.getValue() instanceof Long) {
                Long value = (Long) entry.getValue();
                exerciseNames.add(entry.getKey());
                entries.add(new BarEntry(index++, value));
                Log.d(TAG, entry.getKey() + ": " + value);
                int color = getExerciseColor(entry.getKey());
                colors.add(color);
            }


        }
        BarDataSet dataSet = new BarDataSet(entries, "Exercise Data");
        dataSet.setColors(colors); // Set colors for each bar entry

        dataSet.setValueTextColor(Color.WHITE);



        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(exerciseNames));

        BarData barData = new BarData(dataSet);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(1000);
    }
    private int getExerciseColor(String exerciseName) {
        if (exerciseName.equals("Squats")) {
            return Color.RED;
        } else if (exerciseName.equals("Pushups")) {
            return Color.GREEN;
        } else if (exerciseName.equals("ShoulderPress")) {
            return Color.BLUE;
        } else if (exerciseName.equals("BicepsCurl")) {
            return Color.YELLOW;
        }else {
            return Color.WHITE;
        }

    }
}
package com.google.mlkit.vision.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.github.mikephil.charting.charts.BarChart;
import com.google.mlkit.vision.demo.java.ChooserActivity;

public class DateClass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_class);
        DatePicker datePicker;
        Button saveButton;

        datePicker = findViewById(R.id.datePicker);
        saveButton = findViewById(R.id.saveButton);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int year = datePicker.getYear();
                int month = datePicker.getMonth() + 1; // Month is 0-based
                int day = datePicker.getDayOfMonth();
                String selectedDate =""+ year;
                if(month<10){
                    selectedDate+="0"+month;
                }else{
                    selectedDate+=month;
                }
                if(day<10){
                    selectedDate+="0"+day;
                }else{
                    selectedDate+=day;
                }

                //String selectedDate = year +""+ month +""+ day;
                Log.d("DateClass",selectedDate);
                Intent i = new Intent(DateClass.this, ChartBar.class);
                i.putExtra("Date",selectedDate);
                startActivity(i);
            }
        });



    }
}
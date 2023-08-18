package com.google.mlkit.vision.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

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
                String selectedDate = year + "-" + month + "-" + day;
                Intent i = new Intent(DateClass.this, BarChart.class);
                i.putExtra("Date",selectedDate);
                startActivity(i);
            }
        });



    }
}
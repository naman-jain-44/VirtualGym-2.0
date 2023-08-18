package com.google.mlkit.vision.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;


public class UserActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user);
        LottieAnimationView laview ;
        Button jj =(Button)findViewById(R.id.opentutorial);

        Intent intent = this.getIntent();  //this keyword  is important
        if(intent!=null){

            String name = intent.getStringExtra("name");

            TextView h = (TextView)findViewById(R.id.scode_profile);
         laview =(LottieAnimationView)findViewById(R.id.lottieanimation);
            h.setText((name.toUpperCase()));
            if(name.equals("pushups")){
                laview.setAnimation(R.raw.pushuplottie);
                jj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://www.youtube.com/watch?v=lsRAK6cr5kY");
                        startActivity(new Intent(Intent.ACTION_VIEW,uri));
                    }
                });

            }else if(name.equals("squats")){
                laview.setAnimation(R.raw.squatslottie);
                jj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://www.youtube.com/watch?v=YaXPRqUwItQ");
                        startActivity(new Intent(Intent.ACTION_VIEW,uri));
                    }
                });

            }else if(name.equals("bicepscurl")){
                laview.setAnimation(R.raw.bicepscurllottie);
                jj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://www.youtube.com/watch?v=sYV-ki-1blM");
                        startActivity(new Intent(Intent.ACTION_VIEW,uri));
                    }
                });

            }else if(name.equals("plank")){
                laview.setAnimation(R.raw.planklottie);
                jj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://www.youtube.com/watch?v=yeKv5oX_6GY");
                        startActivity(new Intent(Intent.ACTION_VIEW,uri));
                    }
                });

            }else if(name.equals("crunches")){
                laview.setAnimation(R.raw.cruncheslottie);
                jj.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("https://www.youtube.com/watch?v=0t4t3IpiEao");
                        startActivity(new Intent(Intent.ACTION_VIEW,uri));
                    }
                });

            }
        }


        }

    }

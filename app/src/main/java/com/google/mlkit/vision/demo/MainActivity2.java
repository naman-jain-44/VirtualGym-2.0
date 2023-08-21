package com.google.mlkit.vision.demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            Intent intent = new Intent(this,MainActivity.class);

            startActivity(intent);

            this.finish();
        }
        TextView user =(TextView)findViewById(R.id.user_name);
        FirebaseUser us = FirebaseAuth.getInstance().getCurrentUser();
        user.setText("Hi "+us.getDisplayName().toString());



        TextView ml =(TextView)findViewById(R.id.ml_exercise);
        ml.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(getBaseContext(),EntryChoiceActivity.class);
                startActivity(i);
            }
        });

        TextView learn =(TextView)findViewById(R.id.exercise_learn);
        learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(getBaseContext(),LearnableExerciseList.class);
                startActivity(i);
            }
        });
        TextView timer =(TextView)findViewById(R.id.timer_workout);
        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(getBaseContext(),PersonalWorkoutPlan.class);
                startActivity(i);
            }
        });

        TextView logout = (TextView)findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance().signOut(MainActivity2.this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            startLogin();
                        }
                    }
                });

            }
        });
    }

    void startLogin(){
        Intent intent = new Intent(this,MainActivity.class);

        startActivity(intent);

        this.finish();
    }
}
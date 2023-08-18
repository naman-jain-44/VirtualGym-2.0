package com.google.mlkit.vision.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;


import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;

public class LearnableExerciseList extends AppCompatActivity {


    static int[] imageId = {R.drawable.pushupicon,R.drawable.squaticon,R.drawable.bicepsicon,R.drawable.plankicon,
            R.drawable.crunchesicon,R.drawable.pushupicon,R.drawable.squaticon,R.drawable.bicepsicon,R.drawable.plankicon,
            R.drawable.crunchesicon};
    static String[] name = {"pushups","squats","bicepscurl","plank","crunches","lunges","legpress","legcurls","pulldown","burpee"};
    static HashMap<String ,Integer> hm =new HashMap<>();
    static ListAdapter listAdapter ;
    static ListView c ;
    static ArrayList<User> userArrayList;



    TrieNode root=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learnable_exercise_list);

        root=new TrieNode();
        hm=new HashMap<>();

        for(int i =0;i<imageId.length;i++){
            hm.put(name[i],imageId[i]);
            insert_nodes(name[i],root);
        }



   //by default there are all the lists present in it

        userArrayList = new ArrayList<>();

        for(int i = 0;i< imageId.length;i++){

            User user = new User(name[i],imageId[i]);
            userArrayList.add(user);


        }


        listAdapter = new ListAdapter(LearnableExerciseList.this,userArrayList);
        c = (ListView)findViewById(R.id.listview);

        c.setAdapter(listAdapter);
        c.setClickable(true);
        c.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent i = new Intent(LearnableExerciseList.this,UserActivity.class);
                i.putExtra("name",name[position]);
                startActivity(i);

            }
        });

    }

    private void insert_nodes(String s, TrieNode root) {
        TrieNode x=root;
        for(int i =0;i<s.length();i++){
            char  curr=s.charAt(i);
            if(x.nextnode[curr-'a']!=null){
                x=x.nextnode[curr-'a'];
            }else{
                x.nextnode[curr-'a'] =new TrieNode();
                x=x.nextnode[curr-'a'];
            }
        }
        x.isEnd=true;
        x.required_string=s;
    }

    public void showlist(View view) {

        EditText y =(EditText)findViewById(R.id.search_bar);
        String required=y.getText().toString();
        Log.d("naman",required);

        required=required.trim();

        Log.d("naman",required);
        if(required.equals("")){
            userArrayList = new ArrayList<>();

            for(int i = 0;i< imageId.length;i++){

                User user = new User(name[i],imageId[i]);
                userArrayList.add(user);


            }


            listAdapter = new ListAdapter(LearnableExerciseList.this,userArrayList);
            c = (ListView)findViewById(R.id.listview);

            c.setAdapter(listAdapter);
            c.setClickable(true);


            return ;
        }

        Log.v("naman","hi");

        ArrayList<String> result =new ArrayList<>();
        //search in created trie all the word s starts with
        findWords(required,root,result);




        //set those in the below
        userArrayList=new ArrayList<>();
        for(int i =0;i<result.size();i++){
            User user = new User(result.get(i),hm.get(result.get(i)));
            userArrayList.add(user);
        }

        listAdapter = new ListAdapter(LearnableExerciseList.this,userArrayList);
        c = (ListView)findViewById(R.id.listview);

        c.setAdapter(listAdapter);
        c.setClickable(true);
//        c.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                Intent i = new Intent(LearnableExerciseList.this,UserActivity.class);
//                i.putExtra("name",name[position]);
//                startActivity(i);
//
//            }
//        });






    }

    private void findWords(String required, TrieNode root, ArrayList<String> result) {

        TrieNode x=root;
        for(int i =0;i<required.length();i++){
            char curr=required.charAt(i);
            if(x.nextnode[curr-'a']==null){
                return ;
            }else{
                x=x.nextnode[curr-'a'];
            }

        }

        if(x.isEnd==true){
            result.add(x.required_string);
            return ;
        }else{
            dfs(result , x);
        }
    }

    private void dfs(ArrayList<String> result, TrieNode x) {
        if(x.isEnd==true){
            result.add(new String(x.required_string));
            return ;
        }
        for(int i =0;i<26;i++){
            if(x.nextnode[i]!=null){
                dfs(result , x.nextnode[i]);
            }
        }

    }
}
class TrieNode{
    TrieNode[] nextnode;
    boolean isEnd;
    String required_string;
    TrieNode(){
        nextnode = new TrieNode[26];
        isEnd=false;
    }
}
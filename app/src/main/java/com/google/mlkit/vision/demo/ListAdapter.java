package com.google.mlkit.vision.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListAdapter extends ArrayAdapter<User> {


    public ListAdapter(Context context, ArrayList<User> userArrayList){

        super(context,R.layout.list_item,userArrayList);

    }

    @NotNull
    @Override
    public View getView(int position, @Nullable View convertView, @NotNull ViewGroup parent) {

        User user = getItem(position);

        if (convertView == null){

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,parent,false);

        }

        ImageView imageView = convertView.findViewById(R.id.profile_pic);
        TextView userName = convertView.findViewById(R.id.personName);

        imageView.setImageResource(user.imageId);
        userName.setText(user.name);



        return convertView;
    }
}
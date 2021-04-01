package com.example.classapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class profilepreview extends AppCompatActivity {

    private Animator currentAnimator;
    private int shortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilepreview);

        LayoutInflater inflater = LayoutInflater.from(this);
        View imgPreview_layout = inflater.inflate(R.layout.activity_profilepreview, null);
        ImageView imageView = imgPreview_layout.findViewById(R.id.expanded_image);
        Glide.with(imgPreview_layout.getContext())
                .load(HomePage.profileImage)
                .into(imageView);
        setContentView(imgPreview_layout);

    }

}
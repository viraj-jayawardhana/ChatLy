package com.decima.blogger.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.decima.blogger.R;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView loadingGif = findViewById(R.id.loadingGif);
        Glide.with(this)
                .asGif()
                .load(R.drawable.loading_image) // replace with your GIF image in drawable
                .into(loadingGif);

        new Handler().postDelayed(() -> {
            startActivity(new Intent(SplashScreen.this, ChatListActivity.class));
            finish();
        }, 5000);
    }
}

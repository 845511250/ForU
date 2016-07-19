package com.zuoyun.foru;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends MyBaseActivity {

    Context context;
    private ImageView splash_image;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);
        context = this;

        sharedPreferences = getSharedPreferences("ISFIRSTSTART", MODE_WORLD_WRITEABLE);
        boolean ISFIRSTSTART = sharedPreferences.getBoolean("ISFIRSTSTART", true);

        if(ISFIRSTSTART){
            startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
            finish();
        }
        else
            startAppAnim();
    }

    public void startAppAnim(){
        splash_image = (ImageView) findViewById(R.id.splash_image);
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.splash);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(getApplicationContext(), WebActivity.class));
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        splash_image.startAnimation(animation);
    }
}

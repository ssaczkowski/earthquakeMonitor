package com.ssaczkowski.earthquakemonitor;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends Activity {

    private final int DURACION_SPLASH = 9000; // 3 seconds


    LottieAnimationView animationView;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);


        LottieAnimationView animationView = findViewById(R.id.animation_view_presentation);

        animationView.setAnimation("world_locations.json");
        animationView.loop(true);

        animationView.playAnimation();

        final TextView text_tittle = (TextView) findViewById(R.id.text_view_tittle);

        Animation animBlink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blink);

        text_tittle.setVisibility(View.VISIBLE);
        text_tittle.startAnimation(animBlink);

        new Handler().postDelayed(new Runnable(){
            public void run(){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            };
        }, DURACION_SPLASH);


    }

}


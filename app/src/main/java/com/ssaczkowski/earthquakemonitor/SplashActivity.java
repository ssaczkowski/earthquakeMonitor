package com.ssaczkowski.earthquakemonitor;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;

public class SplashActivity extends Activity {

    private final int DURACION_SPLASH = 9000; // 3 seconds


    LottieAnimationView animationView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);


        LottieAnimationView animationView = findViewById(R.id.animation_view_presentation);

        animationView.setAnimation("world_locations.json");
        animationView.loop(true);

        animationView.playAnimation();

        new Handler().postDelayed(new Runnable(){
            public void run(){
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            };
        }, DURACION_SPLASH);


    }

}


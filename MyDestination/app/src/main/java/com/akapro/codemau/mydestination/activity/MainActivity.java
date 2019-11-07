package com.akapro.codemau.mydestination.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.akapro.codemau.mydestination.R;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity implements Animation.AnimationListener {
    @BindView(R.id.layoutSplashAct)
    RelativeLayout layout;
    @BindView(R.id.imgSlpashAct_Logo)
    ImageView imLogo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Animation transitionAnim = AnimationUtils.loadAnimation(this,R.anim.transition_icon);
        imLogo.setAnimation(transitionAnim);
        Animation alphaAnim = AnimationUtils.loadAnimation(this,R.anim.alpha_background);
        layout.setAnimation(alphaAnim);
        alphaAnim.setAnimationListener(this);
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }
    @Override
    public void onAnimationEnd(Animation animation) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(MainActivity.this,CategoriesActivity.class));
                finish();
            }
        },3000);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}

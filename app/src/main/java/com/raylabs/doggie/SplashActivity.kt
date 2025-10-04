package com.raylabs.doggie;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.raylabs.doggie.databinding.ActivitySplashBinding;
import com.raylabs.doggie.ui.MainActivity;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding splashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        splashBinding = binding;
        setContentView(binding.getRoot());

        setupMotion();
    }

    private void setupMotion() {
        splashBinding.motionLayout.setTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionCompleted(MotionLayout p0, int p1) {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }

            @Override
            public void onTransitionChange(MotionLayout p0, int p1, int p2, float p3) {
            }

            @Override
            public void onTransitionStarted(MotionLayout p0, int p1, int p2) {
            }

            @Override
            public void onTransitionTrigger(MotionLayout p0, int p1, boolean p2, float p3) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        splashBinding.motionLayout.startLayoutAnimation();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUIAndNavigation(this);
        }
    }

    private void hideSystemUIAndNavigation(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            activity.getWindow().setDecorFitsSystemWindows(false);
            WindowInsetsController insetsController = activity.getWindow().getInsetsController();
            if (insetsController != null) {
                insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                insetsController.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            @SuppressWarnings("deprecation")
            View decorView = activity.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            );
        }
    }
}

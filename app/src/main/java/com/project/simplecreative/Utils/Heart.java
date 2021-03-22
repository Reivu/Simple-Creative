package com.project.simplecreative.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

public class Heart {

    private static final String TAG = "Heart";

    public ImageView red, white;

    private static final DecelerateInterpolator DECCELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    public Heart(ImageView red, ImageView white) {
        this.red = red;
        this.white = white;
    }

    public void toggle(){
        AnimatorSet animatorSet = new AnimatorSet();

        if(red.getVisibility() == View.VISIBLE){
            Log.d(TAG, "toggleLike: toggling red heart off.");
            red.setScaleX(0.1f);
            red.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(red, "scaleY", 1f, 0f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(red, "scaleX", 1f, 0f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(ACCELERATE_INTERPOLATOR);

            red.setVisibility(View.GONE);
            white.setVisibility(View.VISIBLE);

            animatorSet.playTogether(scaleDownY, scaleDownX);
        }

        else if(red.getVisibility() == View.GONE){
            Log.d(TAG, "toggleLike: toggling red heart on.");
            red.setScaleX(0.1f);
            red.setScaleY(0.1f);

            ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(red, "scaleY", 0.1f, 1f);
            scaleDownY.setDuration(300);
            scaleDownY.setInterpolator(DECCELERATE_INTERPOLATOR);

            ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(red, "scaleX", 0.1f, 1f);
            scaleDownX.setDuration(300);
            scaleDownX.setInterpolator(DECCELERATE_INTERPOLATOR);

            red.setVisibility(View.VISIBLE);
            white.setVisibility(View.GONE);

            animatorSet.playTogether(scaleDownY, scaleDownX);
        }

        animatorSet.start();

    }
}

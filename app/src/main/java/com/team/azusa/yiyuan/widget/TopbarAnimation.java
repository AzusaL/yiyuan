package com.team.azusa.yiyuan.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by Azusa on 2016/1/2.
 */
public class TopbarAnimation {
    private int translationY;
    private View topView;
    private boolean topbarisShow = true;
    private boolean topbarshowing = false;
    private boolean topbarhideing = false;

    public TopbarAnimation build(int translationY, View topView) {
        this.translationY = translationY;
        this.topView = topView;
        return this;
    }

    public boolean getshowing() {
        return topbarshowing;
    }

    public boolean gethideing() {
        return topbarhideing;
    }

    public boolean getisShow() {
        return topbarisShow;
    }

    public void showTopbar() {
        ObjectAnimator animator = ObjectAnimator.ofFloat(topView, "translationY", 0f);
        ObjectAnimator animator2 = new ObjectAnimator().ofFloat(topView, "alpha", 0f, 1.0f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(300).playTogether(animator, animator2);
        //监听器
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) topView.getLayoutParams();
                float v = (Float) animation.getAnimatedValue();
                layoutParams.height = translationY + (int) v;
                //重新布局
                topView.requestLayout();
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                topbarshowing = true;
                //设置为已显示
                topbarisShow = true;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                topbarshowing = false;
                super.onAnimationEnd(animation);
            }

        });
        //开始动画
        set.start();
    }

    public void hideTopbar() {

        ObjectAnimator animator = ObjectAnimator.ofFloat(topView, "translationY", -translationY);
        ObjectAnimator animator2 = new ObjectAnimator().ofFloat(topView, "alpha", 0.8f, 0f);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(400).playTogether(animator, animator2);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) topView.getLayoutParams();
                float v = (Float) animation.getAnimatedValue();
                layoutParams.height = translationY + (int) v;
                topView.requestLayout();
            }
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                topbarhideing = true;
                topbarisShow = false;
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                topbarhideing = false;
                super.onAnimationEnd(animation);
            }

        });
        set.start();
    }

}

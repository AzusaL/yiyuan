package com.team.azusa.yiyuan.utils;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.team.azusa.yiyuan.R;

/**
 * Created by Delete_exe on 2016/1/19.
 */
public class EditTextShakeHelper {
    private Animation shake;

    public EditTextShakeHelper(Context context) {
        shake = AnimationUtils.loadAnimation(context, R.anim.shake_edit);
    }

    public void shake(View... views) {
        for (View view : views) {
            view.startAnimation(shake);
        }
    }

}

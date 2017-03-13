package com.team.azusa.yiyuan.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.utils.ConstanceUtils;

import java.util.ArrayList;


/**
 * Created by Azusa on 2016/1/16.
 */
public class MyDialog {
    private AlertDialog dialog;
    private ProgressBar progressBar;
    private Context context;

    public AlertDialog showLodingDialog(Activity activity) {
        this.context = activity;
        View view_dialog = View.inflate(activity, R.layout.loding_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        progressBar = (ProgressBar) view_dialog.findViewById(R.id.pb_loding_dialog);
        initAnimation();
        dialog = builder.create();
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        params.width = (int) (display.getWidth() * 0.7);
        params.height = (int) (display.getHeight() * 0.3);
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setContentView(view_dialog);
        dialog.setCancelable(false);
        return dialog;
    }

    public AlertDialog showAddgoodsCarDialog(Context activity, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        dialog = builder.create();
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.setView(view);
        dialog.show();
        dialog.setCancelable(false);
        return dialog;
    }

    public AlertDialog showAddrDialog(View view_dialog, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        dialog = builder.create();
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        WindowManager windowManager = activity.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        params.width = (int) (display.getWidth() * 0.8);
        params.height = (int) (display.getHeight() * 0.8);
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setContentView(view_dialog);
        dialog.setCancelable(false);
        return dialog;
    }

    public AlertDialog showDialog(View view, Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(view);
        return dialog;
    }

    public void initAnimation() {
        Animation animation = AnimationUtils.loadAnimation(context, R.anim.myrotate);
        LinearInterpolator lin = new LinearInterpolator();
        animation.setInterpolator(lin); //设置插值器，匀速加载动画
        progressBar.startAnimation(animation);
    }

    public void dismissDialog() {
        dialog.dismiss();
        if (progressBar != null) {
            progressBar.clearAnimation();
        }
    }

    public void dialogShow() {
        dialog.show();
        initAnimation();
    }
}

package com.team.azusa.yiyuan.utils;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by Delete_exe on 2015/12/25.
 */
public class MyToast extends Toast {
    protected static Toast toast;
    private static String oldMsg;
    private static long oneTime = 0;
    private static long twoTime = 0;

    protected MyToast(Context context) {
        super(context);
    }

    public static void showToast(String s) {
        showToast(s, Gravity.BOTTOM);
    }

    public static void showToast_center(String s) {
        showToast(s, Gravity.CENTER);
    }

    private static void showToast(String s, int gravity) {
        if (toast == null) {
            toast = Toast.makeText(ConstanceUtils.CONTEXT, s, Toast.LENGTH_SHORT);
            toast.setGravity(gravity, 0, 100);
            toast.show();
            oneTime = System.currentTimeMillis();
        } else {
            twoTime = System.currentTimeMillis();
            toast.setGravity(gravity, 0, 100);
            if (s.equals(oldMsg) && twoTime - oneTime > Toast.LENGTH_SHORT) {
                toast.show();
            } else {
                oldMsg = s;
                toast.setText(s);
                toast.show();
            }
        }
        oneTime = twoTime;
    }
}

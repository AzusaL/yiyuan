package com.team.azusa.yiyuan.widget;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.utils.ConstanceUtils;

/**
 * Created by Azusa on 2016/3/23.
 * 重写onMeasure测量textview的高度
 */
public class MyTextView extends TextView {
    public MyTextView(Context context) {
        super(context);
    }

    public MyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = getLayout();
        if (layout != null) {
            int height = (int) Math.ceil(getMaxLineHeight(this.getText().toString()))
                    + getCompoundPaddingTop() + getCompoundPaddingBottom();
            int width = getMeasuredWidth();
            setMeasuredDimension(width, height + 10); //高度加10避免在最后一行某些字符显示不全的情况
        }
    }

    private float getMaxLineHeight(String str) {
        float height = 0.0f;
        float screenW = ConstanceUtils.screenWidth - 3 * getResources().getDimensionPixelOffset(R.dimen.button_margin_vertical);
        float paddingLeft = ((RelativeLayout) this.getParent()).getPaddingLeft();
        float paddingReft = ((RelativeLayout) this.getParent()).getPaddingRight();
        //这里具体this.getPaint()要注意使用，要看你的TextView在什么位置，这个是拿TextView父控件的Padding的，为了更准确的算出换行
        int line = (int) Math.ceil((this.getPaint().measureText(str) / (screenW - paddingLeft - paddingReft)));
        height = (this.getPaint().getFontMetrics().descent - this.getPaint().getFontMetrics().ascent) * line;
        return height;
    }
}

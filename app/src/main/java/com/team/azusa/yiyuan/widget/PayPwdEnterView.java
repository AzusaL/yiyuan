package com.team.azusa.yiyuan.widget;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.team.azusa.yiyuan.R;

/**
 * Created by Azusa on 2016/4/10.
 * 自定义输入密码View
 */
public class PayPwdEnterView extends LinearLayout {
    private EditText mEditText;
    private View oneTextView;
    private View twoTextView;
    private View threeTextView;
    private View fourTextView;
    private View fiveTextView;
    private View sixTextView;
    LayoutInflater inflater;
    View[] imageViews;
    View contentView;

    public PayPwdEnterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = LayoutInflater.from(context);
        builder = new StringBuilder();
        initWidget();
        Log.e("main","-=================---");
    }

    private void initWidget() {
        contentView = inflater.inflate(R.layout.pwd_editview, null);
        mEditText = (EditText) contentView.findViewById(R.id.sdk2_pwd_edit_simple);
        oneTextView = contentView.findViewById(R.id.sdk2_pwd_one_img);
        twoTextView = contentView.findViewById(R.id.sdk2_pwd_two_img);
        fourTextView = contentView.findViewById(R.id.sdk2_pwd_four_img);
        fiveTextView = contentView.findViewById(R.id.sdk2_pwd_five_img);
        sixTextView = contentView.findViewById(R.id.sdk2_pwd_six_img);
        threeTextView = contentView.findViewById(R.id.sdk2_pwd_three_img);

        LinearLayout.LayoutParams lParams = new LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mEditText.addTextChangedListener(mTextWatcher);
        mEditText.setOnKeyListener(keyListener);
        imageViews = new View[]{oneTextView, twoTextView, threeTextView,
                fourTextView, fiveTextView, sixTextView};
        this.addView(contentView, lParams);
    }

    TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == 0) {
                return;
            }

            if (builder.length() < 6) {
                builder.append(s.toString());
                setTextValue();
            }
            s.delete(0, s.length());
        }

    };

    OnKeyListener keyListener = new OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            if (keyCode == KeyEvent.KEYCODE_DEL
                    && event.getAction() == KeyEvent.ACTION_UP) {
                delTextValue();
                return true;
            }
            return false;
        }
    };

    private void setTextValue() {

        String str = builder.toString();
        int len = str.length();

        if (len <= 6) {
            imageViews[len - 1].setVisibility(View.VISIBLE);
        }
        if (len == 6) {
            if (mListener != null) {
                mListener.onNumCompleted(str);
            }
        }
    }

    private void delTextValue() {
        String str = builder.toString();
        int len = str.length();
        if (len == 0) {
            return;
        }
        if (len > 0 && len <= 6) {
            builder.delete(len - 1, len);
        }
        imageViews[len - 1].setVisibility(View.INVISIBLE);
        ;
    }

    StringBuilder builder;

    public interface SecurityEditCompleListener {
        public void onNumCompleted(String num);
    }

    public SecurityEditCompleListener mListener;

    public void setSecurityEditCompleListener(
            SecurityEditCompleListener mListener) {
        this.mListener = mListener;
    }

    public void clearSecurityEdit() {
        if (builder != null) {
            if (builder.length() == 6) {
                builder.delete(0, 6);
            }
        }
        for (View tv : imageViews) {
            tv.setVisibility(View.INVISIBLE);
        }

    }

    public EditText getSecurityEdit() {
        return this.mEditText;
    }
}


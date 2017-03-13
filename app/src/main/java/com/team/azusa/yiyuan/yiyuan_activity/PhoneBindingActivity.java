package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PhoneBindingActivity extends AppCompatActivity {

    @Bind(R.id.phone_number)
    TextView phoneNumber;
    @Bind(R.id.et_verification_code)
    EditText etVerificationCode;
    @Bind(R.id.phone_binding_get_ver)
    Button phoneBindingGetVer;
    @Bind(R.id.phone_binding_save)
    Button phoneBindingSave;
    private String userPhoneNumber;
    private int second;
    private Runnable runnable = new TimerTask() {

        @Override
        public void run() {
            if (second == 0) {
                phoneBindingGetVer.setText("获取验证码");
                phoneBindingGetVer.setClickable(true);
                phoneBindingGetVer.setBackgroundResource(R.drawable.button_envy);
                handler.removeCallbacks(this);
                return;
            }
            phoneBindingGetVer.setText("重新获取(" + second + ")");
            second--;
            handler.postDelayed(this, 1000);
        }
    };
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_binding);
        ButterKnife.bind(this);
        userPhoneNumber = UserUtils.user.getMobile();
        userPhoneNumber = userPhoneNumber.substring(0, 5) + "****" + userPhoneNumber.substring(9, 11);
        phoneNumber.setText(userPhoneNumber);
    }

    //获取验证码
    public void getVer() {
        second = 60 - (int) ((System.currentTimeMillis() - ConstanceUtils.THE_COUNT_DOWN_TIME) / 1000);
        if (second > 0) {
            MyToast.showToast_center("请求过于频繁，请稍后再试");
        } else {
            OkHttpUtils.get()
                    .url(Config.IP + "/yiyuan/user_sendMsg")
                    .addParams("mobile", UserUtils.user.getMobile())
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    MyToast.showToast("修改出错");
                }

                @Override
                public void onBefore(Request request) {
                    Log.e("绑定手机获取验证码接口", request.urlString());
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        if (message.equals("success")) {
                            UserUtils.sendConfirmCodeTo(UserUtils.user.getMobile());
                            MyToast.showToast_center("验证码已发，请注意查收");
                            phoneBindingGetVer.setBackgroundResource(R.drawable.btn_envy_noenable);
                            phoneBindingGetVer.setClickable(false);
                            second = 60 - (int) ((System.currentTimeMillis() - ConstanceUtils.THE_COUNT_DOWN_TIME) / 1000);
                            handler.post(runnable);
                            phoneBindingSave.setClickable(true);
                            phoneBindingSave.setBackgroundResource(R.drawable.button_envy);
                        }
                        if (message.equals("fail"))
                            MyToast.showToast_center("获取验证码失败");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    //确认验证码
    public void saveVer() {
        OkHttpUtils.post()
                .url(Config.IP + "/yiyuan/user_checkMsgCode")
                .addParams("mobile", UserUtils.user.getMobile())
                .addParams("msgCode", etVerificationCode.getText().toString())
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                MyToast.showToast("修改出错");
            }

            @Override
            public void onBefore(Request request) {
                Log.e("绑定手机确认验证码接口", request.urlString());
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    MyToast.showToast_center(message);
                    if (message.equals("success")) {
                        Intent startInputPayPassword = new Intent(PhoneBindingActivity.this, IntPutPhoneActivity.class);
                        startActivityForResult(startInputPayPassword, ConstanceUtils.PhoneBindingActivity_ForResult);
                    }
                    if (message.equals("fail"))
                        MyToast.showToast_center("验证码出错");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void myFinish(String str) {
        Intent intent = new Intent();
        intent.putExtra("result", str);
        setResult(ConstanceUtils.PhoneBindingActivitySetResult, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        myFinish("finish");
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result = data.getStringExtra("result");
        if (result.equals("finish"))
            myFinish("");
        else if (result.equals("finish2"))
            myFinish("finish");
        else if (result.equals("finish3"))
            myFinish("finish");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.phone_binding_go_back, R.id.phone_binding_get_ver, R.id.phone_binding_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.phone_binding_go_back:
                myFinish("");
                break;
            case R.id.phone_binding_get_ver:
                getVer();
                break;
            case R.id.phone_binding_save:
                if (StringUtil.isEmail(etVerificationCode.getText().toString())) {
                    MyToast.showToast_center("请输入验证码");
                } else saveVer();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}

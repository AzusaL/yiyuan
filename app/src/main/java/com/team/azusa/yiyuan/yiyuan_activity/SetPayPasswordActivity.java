package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.WiperSwitch;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetPayPasswordActivity extends AppCompatActivity {

    @Bind(R.id.phone_number)
    TextView phoneNumber;
    @Bind(R.id.et_verification_code)
    EditText etVerificationCode;
    @Bind(R.id.set_pp_ll)
    LinearLayout setppLl;
    @Bind(R.id.pay_mima_wiperSwitch)
    WiperSwitch payMimaWiperSwitch;
    @Bind(R.id.set_pay_mima_save)
    Button btnSave;
    private Button setPayGetVer;
    private String userPhoneNumber;
    private int second;
    private Handler handler = new Handler();
    private Runnable runnable = new TimerTask() {
        @Override
        public void run() {
            if (second == 0) {
                setPayGetVer.setText("获取验证码");
                setPayGetVer.setClickable(true);
                setPayGetVer.setBackgroundResource(R.drawable.button_envy);
                handler.removeCallbacks(this);
                return;
            }
            setPayGetVer.setText("重新获取(" + second + ")");
            second--;
            handler.postDelayed(this, 1000);
        }
    };

    //    class myTimerTask extends TimerTask{
//        private Button btnGetVer;
//        private int second;
//        private Handler handler;
//        @Override
//        public void run() {
//            if (second == 0) {
//                btnGetVer.setText("获取验证码");
//                btnGetVer.setClickable(true);
//                btnGetVer.setBackgroundResource(R.drawable.button_envy);
//                handler.removeCallbacks(this);
//                return;
//            }
//            btnGetVer.setText("重新获取(" + second + ")");
//            second--;
//            handler.postDelayed(this,1000);
//        }
//
//        protected myTimerTask(Button btnGetVer,int second,Handler handler) {
//            this.btnGetVer = btnGetVer;
//            this.second = second;
//            this.handler = handler;
//        }
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_pay_password);
        ButterKnife.bind(this);
        setPayGetVer = (Button) findViewById(R.id.set_pay_get_ver);
        userPhoneNumber = UserUtils.user.getMobile();
        userPhoneNumber = userPhoneNumber.substring(0, 5) + "****" + userPhoneNumber.substring(9, 11);
        phoneNumber.setText(userPhoneNumber);
        payMimaWiperSwitch.setChecked(true);
        payMimaWiperSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    setppLl.setVisibility(View.VISIBLE);
                else
                    setppLl.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onRestart() {
        setppLl.setVisibility(View.GONE);
        payMimaWiperSwitch.setChecked(false);
        super.onRestart();
    }

    //获取验证码
    public void getVer() {
        second = 60 - (int) ((System.currentTimeMillis() - ConstanceUtils.THE_COUNT_DOWN_TIME) / 1000);
        if (second > 0) {
            MyToast.showToast_center("请求过于频繁，请稍后再试");
        } else {
            OkHttpUtils.post()
                    .url(Config.IP + "/yiyuan/user_sendMsg")
                    .addParams("mobile", UserUtils.user.getMobile())
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    MyToast.showToast("修改出错");
                }

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("message");
                        if (message.equals("success")) {
                            UserUtils.sendConfirmCodeTo(UserUtils.user.getMobile());
                            MyToast.showToast_center("验证码已发，请注意查收");
                            setPayGetVer.setBackgroundResource(R.drawable.btn_envy_noenable);
                            setPayGetVer.setClickable(false);
                            second = 60 - (int) ((System.currentTimeMillis() - ConstanceUtils.THE_COUNT_DOWN_TIME) / 1000);
                            handler.post(runnable);
                            btnSave.setBackgroundResource(R.drawable.button_envy);
                            btnSave.setClickable(true);
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
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    MyToast.showToast_center(message);
                    if (message.equals("success")) {
                        Intent startInputPayPassword = new Intent(SetPayPasswordActivity.this, InputPayPasswordActivity.class);
                        startActivityForResult(startInputPayPassword, ConstanceUtils.SetPayPasswordActivity_ForResult);
                    }
                    if (message.equals("fail"))
                        MyToast.showToast_center("验证码出错");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.set_mima_go_back, R.id.set_pay_get_ver, R.id.set_pay_mima_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_mima_go_back:
                myFinish("");
                break;
            case R.id.set_pay_get_ver:
                getVer();
                break;
            case R.id.set_pay_mima_save:
                if (StringUtil.isEmail(etVerificationCode.getText().toString())) {
                    MyToast.showToast_center("请输入验证码");
                } else saveVer();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        myFinish("finish");
        super.onBackPressed();
    }

    private void myFinish(String str) {
        Intent intent = new Intent();
        intent.putExtra("result", str);
        setResult(ConstanceUtils.SetPayPasswordActivitySetResult, intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result = data.getStringExtra("result");
        if (result.equals("finish"))
            myFinish("");
        else if (result.equals("finish2"))
            myFinish("finish");
//        if (result.equals("finish3"))
//            myFinish("finish2");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}
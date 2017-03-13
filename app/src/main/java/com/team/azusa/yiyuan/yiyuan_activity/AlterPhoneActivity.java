package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONObject;

import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlterPhoneActivity extends AppCompatActivity {

    @Bind(R.id.alter_phone_edit)
    ClearEditText alterPhoneEdit;
    @Bind(R.id.alter_phone_second)
    TextView alterPhoneSecond;
    @Bind(R.id.alter_phone_again)
    Button alterPhoneAgain;
    @Bind(R.id.alter_phone_tx)
    TextView alterPhoneTx;
    @Bind(R.id.tx1)
    TextView tx1;
    @Bind(R.id.tx2)
    TextView tx2;
    private boolean cancelreq = false;
    private String phone;
    private String phone_;
    private String phonetv;
    private int second;
    private Handler handler = new Handler();
    private Runnable runnable = new TimerTask() {
        @Override
        public void run() {
            if (second == 0) {
//                setPayGetVer.setText("获取验证码");
//                setPayGetVer.setClickable(true);
//                setPayGetVer.setBackgroundResource(R.drawable.button_envy);
//                handler.removeCallbacks(this);
//                return;
                tx1.setText("如未收到验证短信，请");
                alterPhoneSecond.setVisibility(View.GONE);
                tx2.setText("点击重新发送。");
                alterPhoneAgain.setEnabled(true);
                handler.removeCallbacks(this);
                alterPhoneAgain.setBackgroundResource(R.drawable.button_envy);
                return;
            }
//            setPayGetVer.setText("重新获取(" + second + ")");
//            second--;
//            handler.postDelayed(this, 1000);
            alterPhoneSecond.setText(second + "");
            second--;
            handler.postDelayed(this, 1000);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_phone);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        phone = intent.getStringExtra("newPhone");
        if (StringUtil.isMobileNo(phone)) {
            phone_ = phone.substring(0, 5) + "****" + phone.substring(9, 11);
        }
        phonetv = "已将验证码发送至" + phone_ + "，请注意查收";
        alterPhoneTx.setText(phonetv);
        second = 60 - (int) ((System.currentTimeMillis() - ConstanceUtils.THE_COUNT_DOWN_TIME) / 1000);
        if (second < 0)
            second = 0;
        handler.post(runnable);
    }

    private void updateSendAgainView() {
        UserUtils.sendConfirmCodeTo(UserUtils.user.getMobile());
        second = 60 - (int) ((System.currentTimeMillis() - ConstanceUtils.THE_COUNT_DOWN_TIME) / 1000);
        tx1.setText("如未收到验证短信，请在");
        alterPhoneSecond.setVisibility(View.VISIBLE);
        tx2.setText("秒后点击重新发送。");
        handler.post(runnable);
        //还没写重新请求发验证码短信
    }

    private void myFinish(String str) {
        Intent intent = new Intent();
        intent.putExtra("result", str);
        setResult(ConstanceUtils.IntPutPhoneActivity_ForResult, intent);
        finish();
    }

    //确认验证码
    public void saveVer() {
        OkHttpUtils.get()
                .url(Config.IP + "/yiyuan/user_changeMobile")
                .addParams("mobile", phone)
                .addParams("userId", UserUtils.user.getId())
                .addParams("msgCode", alterPhoneEdit.getText().toString())
                .tag("AlterPhoneActivity")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelreq) {
                    return;
                }
                MyToast.showToast("修改出错");
            }

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    MyToast.showToast_center(message);
                    if (message.equals("success")) {
                        MyToast.showToast_center("修改成功");
                        UserUtils.user.setMobile(phone);
                        myFinish("finish3");
                    }
                    if (message.equals("fail"))
                        MyToast.showToast_center("验证码出错");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @OnClick({R.id.return_intput_phone, R.id.alter_phone_ensure, R.id.alter_phone_again})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_intput_phone:
                myFinish("");
                break;
            case R.id.alter_phone_ensure:
                String confirmCode = alterPhoneEdit.getText().toString();
                if (!(confirmCode.length() == 6 && StringUtil.isNumber(confirmCode))) {
                    MyToast.showToast("请输入6位数字的验证码");
                    return;
                } else saveVer();

                break;
            case R.id.alter_phone_again:
                alterPhoneAgain.setBackgroundResource(R.drawable.btn_envy_noenable);
                alterPhoneAgain.setEnabled(false);
                updateSendAgainView();
//                UserUtils.sendConfirmCodeTo(phone);
//                handler.post(runnable);
                break;
        }
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        cancelreq = true;
        OkHttpUtils.getInstance().cancelTag("AlterPhoneActivity");
        super.onDestroy();
    }
}

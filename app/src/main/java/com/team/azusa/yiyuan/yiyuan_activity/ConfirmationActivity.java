package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmationActivity extends Activity {

    @Bind(R.id.confirmation_edit)
    ClearEditText confirmationEdit;
    @Bind(R.id.confirmation_second)
    TextView confirmationSecond;
    @Bind(R.id.tx1)
    TextView tx1;
    @Bind(R.id.tx2)
    TextView tx2;
    @Bind(R.id.confirmation_again)
    Button confirmation_again;
    private boolean cancelreq;
    private String phone;
    private int type;
    private String time;//时间戳
    int second = 120 - (int) (System.currentTimeMillis() - ConstanceUtils.THE_COUNT_DOWN_TIME) / 1000;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (second == 0) {
                tx1.setText("如未收到验证短信，请");
                confirmationSecond.setVisibility(View.GONE);
                tx2.setText("点击重新发送。");
                confirmation_again.setEnabled(true);
                handler.removeCallbacks(this);
                return;
            }
            confirmationSecond.setText(second + "");
            second--;
            handler.postDelayed(this, 1000);
        }
    };

    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        ButterKnife.bind(this);
        phone = getIntent().getStringExtra("phone");
        type = getIntent().getIntExtra("type", 0);
        handler.post(runnable);
    }

    @OnClick({R.id.return_confirmation, R.id.confirmation_ensuer, R.id.confirmation_again})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_confirmation:
                finish();
                break;
            case R.id.confirmation_again:
                updateSendAgainView();
                UserUtils.sendConfirmCodeTo(phone);
                break;
            case R.id.confirmation_ensuer:
                String confirmCode = confirmationEdit.getText().toString();
                if (confirmCode.length() < 6) {
                    MyToast.showToast("验证码长度不足");
                    return;
                }

                OkHttpUtils.get().tag("ConfirmationActivity")
                        .url(Config.IP + "/yiyuan//user_checkMsgCode")
                        .addParams("mobile", phone)
                        .addParams("msgCode", confirmCode)
                        .tag("ConfirmationActivity")
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (cancelreq) {
                            return;
                        }
                        MyToast.showToast("网络连接出错");
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            time = jsonObject.getString("time");

                            if ("fail".equals(message)) {
                                MyToast.showToast_center("验证码错误");
                            } else if ("success".equals(message)) {
                                MyToast.showToast_center("验证成功");
                                Intent intent = new Intent(ConfirmationActivity.this, NewPasswordActivity.class);
                                intent.putExtra("phone", phone);
                                intent.putExtra("type", type);
                                intent.putExtra("time", time);
                                startActivityForResult(intent, 200);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
    }

    private void updateSendAgainView() {
        second = 8;
        tx1.setText("如未收到验证短信，请在");
        confirmationSecond.setVisibility(View.VISIBLE);
        tx2.setText("秒后点击重新发送。");
        confirmation_again.setEnabled(false);
        handler.post(runnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 200 && resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
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
        OkHttpUtils.getInstance().cancelTag("ConfirmationActivity");
        super.onDestroy();
    }
}

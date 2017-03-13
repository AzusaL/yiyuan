package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgetPasswordActivity extends Activity {

    @Bind(R.id.forgetPassword_edit)
    ClearEditText forgetPasswordEdit;
    private boolean cancelreq = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.return_forgetPassword, R.id.forgetPassword_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_forgetPassword:
                finish();
                break;
            case R.id.forgetPassword_ensure:
                if (ConstanceUtils.THE_COUNT_DOWN_TIME != 0L &&
                        System.currentTimeMillis() - ConstanceUtils.THE_COUNT_DOWN_TIME < 120000) {
                    MyToast.showToast_center("不能过频繁请求");
                    return;
                }
                final String Phone = forgetPasswordEdit.getText().toString();
                if (!StringUtil.isMobileNo(Phone)) {
                    MyToast.showToast("号码不是手机号");
                    return;
                }
                OkHttpUtils.get().tag("ForgetPasswordActivity")
                        .url(Config.IP + "/yiyuan/user_forgetPwd")
                        .addParams("mobile", Phone)
                        .tag("ForgetPasswordActivity")
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (cancelreq) {
                            return;
                        }
                        MyToast.showToast("网络连接错误");
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            UserUtils.sendConfirmCodeTo(Phone);
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            if ("null".equals(message)) {
                                MyToast.showToast_center("用户不存在");
                            } else if ("fail".equals(message)) {
                                MyToast.showToast_center("发送验证码失败");
                            } else if ("success".equals(message)) {
                                MyToast.showToast_center("验证码发送成功");
                                Intent intent = new Intent(ForgetPasswordActivity.this, ConfirmationActivity.class);
                                intent.putExtra("phone", Phone);
                                intent.putExtra("type", 321);
                                startActivityForResult(intent, 100);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100 && resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        cancelreq = true;
        OkHttpUtils.getInstance().cancelTag("ForgetPasswordActivity");
        super.onDestroy();
    }
}

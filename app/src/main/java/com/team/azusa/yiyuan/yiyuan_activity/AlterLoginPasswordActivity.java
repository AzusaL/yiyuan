package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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
import com.zhy.http.okhttp.cookie.PersistentCookieStore;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.CookieManager;
import java.net.CookiePolicy;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlterLoginPasswordActivity extends AppCompatActivity {

    @Bind(R.id.account_name)
    TextView accountName;
    @Bind(R.id.et_now_password)
    EditText etNowPassword;
    @Bind(R.id.et_new_password)
    EditText etNewPassword;
    @Bind(R.id.et_ensure_password)
    EditText etEnsurePassword;
    private String phoneNumber;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
    private String message;
    private boolean cancelreq = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_login_password);
        ButterKnife.bind(this);
        phoneNumber = UserUtils.user.getMobile();
        phoneNumber = phoneNumber.substring(0, 5) + "****" + phoneNumber.substring(9, 11);
        accountName.setText(phoneNumber);
    }

    //注销
    private void unLogin() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/user_UserLoginOut")
                .tag("AlterLoginPasswordActivity")
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
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    String unloginResponses = jsonObject.get("messageInfo").toString();
                    if ("退出成功".equals(unloginResponses)) {
                        UserUtils.userisLogin = false;
                        UserUtils.user = null;
                        OkHttpUtils.getInstance().getOkHttpClient().setCookieHandler(new CookieManager(
                                new PersistentCookieStore(getApplicationContext()),
                                CookiePolicy.ACCEPT_ALL));
                        Intent intent = new Intent(AlterLoginPasswordActivity.this, LoginActivity.class);
                        startActivity(intent);
                        myFinish("finish2");
                    } else {
                        MyToast.showToast("网络连接出错");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    //修改登录密码
    private void alterLoginPassword() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/user_updatePwd")
                .addParams("oldPassword", oldPassword)
                .addParams("newPassword", newPassword)
                .tag("AlterLoginPasswordActivity")
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
                    message = jsonObject.getString("messageInfo");
                    MyToast.showToast_center(message);
                    if (message.equals("修改成功")) {
                        unLogin();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void myFinish(String str) {
        Intent intent = new Intent();
        intent.putExtra("result", str);
        setResult(ConstanceUtils.AlterLoginPasswordActivitySetResult, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        myFinish("finish");
        super.onBackPressed();
    }

    @OnClick({R.id.alter_mima_go_back, R.id.btn_save})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.alter_mima_go_back:
                myFinish("");//myFinish("");等于finish();
                break;
            case R.id.btn_save:
                oldPassword = etNowPassword.getText().toString();
                newPassword = etNewPassword.getText().toString();
                confirmPassword = etEnsurePassword.getText().toString();
                if (StringUtil.isEmail(oldPassword))
                    MyToast.showToast_center("当前密码不能为空");
                else if (StringUtil.isEmail(newPassword))
                    MyToast.showToast_center("新密码不能为空");
                else if (newPassword.length() < 8 || newPassword.length() > 20)
                    MyToast.showToast_center("密码长度为8-20位");
                else if (!StringUtil.isTruePassword(newPassword))
                    MyToast.showToast_center("密码由字母,数组或符号两种或以上组成");
                else if (!newPassword.equals(confirmPassword))
                    MyToast.showToast_center("两次密码输入不一致，请重新输入");

                else alterLoginPassword();

                break;
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        cancelreq = true;
        OkHttpUtils.getInstance().cancelTag("AlterLoginPasswordActivity");
        super.onDestroy();
    }
}

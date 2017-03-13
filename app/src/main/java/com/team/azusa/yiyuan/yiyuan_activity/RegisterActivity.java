package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

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

public class RegisterActivity extends Activity {

    @Bind(R.id.regisiter_edit)
    ClearEditText regisiterEdit;
    @Bind(R.id.cb_newpass)
    CheckBox cbNewpass;
    @Bind(R.id.regisiter_ensure)
    Button ensure;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        cbNewpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ensure.setEnabled(isChecked);
            }
        });
    }

    @OnClick({R.id.return_regisiter, R.id.regisiter_ensure, R.id.regisiter_tx})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_regisiter:
                finish();
                break;
            case R.id.regisiter_tx:
                Intent service = new Intent(RegisterActivity.this, ServiceAgreementActivity.class);
                startActivity(service);
                break;
            case R.id.regisiter_ensure:
                if (ConstanceUtils.THE_COUNT_DOWN_TIME != 0L &&
                        System.currentTimeMillis() - ConstanceUtils.THE_COUNT_DOWN_TIME < 120000) {
                    MyToast.showToast_center("不能过频繁请求");
                    return;
                }
                final String Phone = regisiterEdit.getText().toString();
                if (!StringUtil.isMobileNo(Phone)) {
                    MyToast.showToast("号码不是手机号");
                    return;
                }
                OkHttpUtils.get().tag("RegisterActivity")
                        .url(Config.IP + "/yiyuan/user_registSendCode")
                        .addParams("mobile", Phone)
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        MyToast.showToast("网络连接出错");
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            if ("exist".equals(message)) {
                                MyToast.showToast_center("用户已存在");
                            } else if ("fail".equals(message)) {
                                MyToast.showToast_center("发送验证码失败");
                            } else if ("success".equals(message)) {
                                UserUtils.sendConfirmCodeTo(Phone);
                                MyToast.showToast_center("验证码发送成功");
                                Intent intent = new Intent(RegisterActivity.this, ConfirmationActivity.class);
                                intent.putExtra("phone", Phone);
                                intent.putExtra("type", 123);
                                startActivityForResult(intent, 1000);
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
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}

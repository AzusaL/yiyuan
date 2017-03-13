package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.widget.ClearEditText;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewPasswordActivity extends Activity {

    @Bind(R.id.newpass_edit)
    ClearEditText newpassEdit;
    @Bind(R.id.cb_newpass)
    CheckBox cbNewpass;

    private String phone;
    private int type;
    private String time;
    private boolean cancelrequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password);
        phone = getIntent().getStringExtra("phone");
        type = getIntent().getIntExtra("type", 0);
        time = getIntent().getStringExtra("time");
        ButterKnife.bind(this);
        cbNewpass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int type = isChecked ? (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) :
                        (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                newpassEdit.setInputType(type);
                newpassEdit.setSelection(newpassEdit.getText().toString().length());
            }
        });
    }

    @OnClick({R.id.return_newpassword, R.id.newpass_ensuer})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_newpassword:
                finish();
                break;
            case R.id.newpass_ensuer:
                final String pwd = newpassEdit.getText().toString();

                if (StringUtil.isEmail(pwd))
                    MyToast.showToast_center("密码不能为空");
                else if (pwd.length() < 8 || pwd.length() > 20)
                    MyToast.showToast_center("密码长度为8-20位");
                else if (!StringUtil.isTruePassword(pwd))
                    MyToast.showToast_center("密码由字母,数组或符号两种或以上组成");

                else
                OkHttpUtils.get().tag("NewPasswordActivity")
                        .url(Config.IP + "/yiyuan/user_reSetPwd")
                        .addParams("mobile", phone)
                        .addParams("time", time)
                        .addParams("pwd", pwd)
                        .tag("NewPasswordActivity")
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Request request, Exception e) {
                        if (cancelrequest) {
                            return;
                        }
                        MyToast.showToast("网络连接出错");
                    }

                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            if ("fail".equals(message)) {
                                MyToast.showToast_center("验证码错误");
                            } else if ("success".equals(message)) {
                                MyToast.showToast_center("验证成功");
                                switch (type) {
                                    case 123:
                                        MyToast.showToast_center("用户注册成功");
                                        break;
                                    case 321:
                                        MyToast.showToast_center("密码修改成功");
                                        break;
                                }
                                setResult(RESULT_OK);
                                finish();
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
    protected void onDestroy() {
        ButterKnife.unbind(this);
        OkHttpUtils.getInstance().cancelTag("NewPasswordActivity");
        super.onDestroy();
    }
}

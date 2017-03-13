package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InputPayPasswordActivity extends AppCompatActivity {

    @Bind(R.id.checkBox)
    CheckBox checkBox;
    @Bind(R.id.newPayPassword)
    EditText newPayPassword;
    @Bind(R.id.confirmPayPassword)
    EditText confirmPayPassword;
    private boolean cancelreq = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_pay_password);
        ButterKnife.bind(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    newPayPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmPayPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    newPayPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmPayPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
    }

    private void myFinish(String str) {
        Intent intent = new Intent();
        intent.putExtra("result", str);
        setResult(ConstanceUtils.SetPayPasswordActivity_ForResult, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        myFinish("finish2");
        super.onBackPressed();
    }

    //修改密码
    private void save_pay_Password() {
        OkHttpUtils.get()
                .url(Config.IP + "/yiyuan/user_changePayPwd")
                .addParams("payPwd", newPayPassword.getText().toString())
                .tag("InputPayPasswordActivity")
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
                    String message = jsonObject.get("message").toString();
                    if (message.equals("success")) {
                        MyToast.showToast_center("修改成功");
                        UserUtils.user.setPayPwd(newPayPassword.getText().toString());
                        Thread.sleep(1000);
                        myFinish("finish2");
                    } else if (message.equals("fail")) {
                        MyToast.showToast_center("修改失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @OnClick({R.id.input_mima_go_back, R.id.save_pay_Password})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.input_mima_go_back:
                myFinish("");
                break;
            case R.id.save_pay_Password:

                if (!StringUtil.isNumber(newPayPassword.getText().toString() + "" + confirmPayPassword.getText().toString())
                        || newPayPassword.getText().toString().length() < 6 || confirmPayPassword.getText().toString().length() < 6) {
                    MyToast.showToast_center("请输入6位数字密码");
                } else if (newPayPassword.getText().toString().equals(confirmPayPassword.getText().toString())) {
                    save_pay_Password();
                } else MyToast.showToast_center("两次密码不同");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        cancelreq = true;
        OkHttpUtils.getInstance().cancelTag("InputPayPasswordActivity");
        super.onDestroy();
    }
}

package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class IntPutPhoneActivity extends AppCompatActivity {

    @Bind(R.id.intput_phone_edit)
    ClearEditText intputPhoneEdit;
    private String phone;
    private boolean cancelreq = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_int_put_phone);
        ButterKnife.bind(this);
    }

    private void myFinish(String str) {
        Intent intent = new Intent();
        intent.putExtra("result", str);
        setResult(ConstanceUtils.PhoneBindingActivity_ForResult, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        myFinish("finish2");
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String result = data.getStringExtra("result");
        if (result.equals("finish"))
            myFinish("");
        if (result.equals("finish2"))
            myFinish("finish");
        if (result.equals("finish3"))
            myFinish("finish2");
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick({R.id.return_intput_phone, R.id.intput_phone_ensure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_intput_phone:
                myFinish("");
                break;
            case R.id.intput_phone_ensure:
                phone = intputPhoneEdit.getText().toString();
//                if (ConstanceUtils.THE_COUNT_DOWN_TIME != 0L &&
//                        System.currentTimeMillis() - ConstanceUtils.THE_COUNT_DOWN_TIME < 120000) {
//                    MyToast.showToast_center("不能过频繁请求");
//                    return;
//                }
                if (!StringUtil.isMobileNo(phone)) {
                    MyToast.showToast("号码不是手机号");
                    return;
                }
                OkHttpUtils.get().url(Config.IP + "/yiyuan/user_registSendCode")
                        .addParams("mobile", phone)
                        .tag("IntPutPhoneActivity")
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
                            JSONObject jsonObject = new JSONObject(response);
                            String message = jsonObject.getString("message");
                            if ("success".equals(message)) {
                                UserUtils.sendConfirmCodeTo(phone);
                                MyToast.showToast_center("验证码发送成功");
                                Intent starAlterPhoneActivity = new Intent(IntPutPhoneActivity.this, AlterPhoneActivity.class);
                                starAlterPhoneActivity.putExtra("newPhone", phone);
                                startActivityForResult(starAlterPhoneActivity, ConstanceUtils.IntPutPhoneActivity_ForResult);
                            } else if ("fail".equals(message))
                                MyToast.showToast_center("发送验证码失败");
                            else if ("exist".equals(message))
                                MyToast.showToast_center("该号码已注册");
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
        cancelreq = true;
        OkHttpUtils.getInstance().cancelTag("IntPutPhoneActivity");
        super.onDestroy();
    }
}

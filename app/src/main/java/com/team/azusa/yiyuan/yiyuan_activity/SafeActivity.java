package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.utils.UserUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SafeActivity extends AppCompatActivity {

    @Bind(R.id.safe_go_back)
    ImageView safeGoBack;
    @Bind(R.id.tv_login_password)
    TextView tvLoginPassword;
    @Bind(R.id.tv_pay_password)
    TextView tvPayPassword;
    //    @Bind(R.id.tv_no_password)
//    TextView tvNoPassword;
    @Bind(R.id.tv_phone_binding)
    TextView tvPhoneBinding;
    //    @Bind(R.id.tv_email_binding)
//    TextView tvEmailBinding;
//    @Bind(R.id.tv_login_protection)
//    TextView tvLoginProtection;
    @Bind(R.id.user_phone_number)
    TextView userPhoneNumber;
    private Drawable drawable;
    private String uerPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe);
        ButterKnife.bind(this);
        showdrawableLeft();
    }

    private void showdrawableLeft() {
        drawable = SafeActivity.this.getResources().getDrawable(R.drawable.pay_success);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());//必须设置图片大小，否则不显示
        if (UserUtils.userisLogin) {
            tvLoginPassword.setCompoundDrawables(drawable, null, null, null);
            tvPhoneBinding.setCompoundDrawables(drawable, null, null, null);
            uerPhone = UserUtils.user.getMobile();
            uerPhone = uerPhone.substring(0, 5) + "****" + uerPhone.substring(9, 11);
            userPhoneNumber.setText(uerPhone);
        }
        if (!StringUtil.isEmpty(UserUtils.user.getPayPwd())) {
            tvPayPassword.setCompoundDrawables(drawable, null, null, null);
        }

    }

    @Override
    public void onBackPressed() {
        myFinish("noFinish");
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ConstanceUtils.SafeActivity_ForResult && (
                resultCode == 302 || resultCode == 303 || resultCode == 304)) {
            String result = data.getStringExtra("result");
            if (result.equals("finish"))
                myFinish("onFinish");
            else if (result.equals("finish2"))
                myFinish("finish");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void myFinish(String str){
        Intent intent = new Intent();
        intent.putExtra("result", str);
        setResult(ConstanceUtils.SafeActivitySetResult, intent);
        finish();
    }


    @OnClick({R.id.safe_go_back, R.id.login_password, R.id.rl_pay_password, R.id.rl_phone_binding})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.safe_go_back:
                myFinish("");
                break;
            case R.id.login_password:
                Intent startAlterLoginPassword = new Intent(SafeActivity.this, AlterLoginPasswordActivity.class);
                startActivityForResult(startAlterLoginPassword, ConstanceUtils.SafeActivity_ForResult);
                break;
            case R.id.rl_pay_password:
                Intent startSetPayPassword = new Intent(SafeActivity.this, SetPayPasswordActivity.class);
                startActivityForResult(startSetPayPassword, ConstanceUtils.SafeActivity_ForResult);
                break;
//            case R.id.rl_no_password:
//                break;
            case R.id.rl_phone_binding:
                Intent startPhoneBinding = new Intent(SafeActivity.this, PhoneBindingActivity.class);
                startActivityForResult(startPhoneBinding, ConstanceUtils.SafeActivity_ForResult);
                break;
//            case R.id.rl_email_binding:
//                startActivity(new Intent(SafeActivity.this, EmailBindingActivity.class));
//                break;
//            case R.id.rl_login_protection:
//                startActivity(new Intent(SafeActivity.this, ProtectLoginActivity.class));
//                break;
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}

package com.team.azusa.yiyuan.yiyuan_activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.utils.UserUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlterSignatureActivity extends AppCompatActivity {

    @Bind(R.id.et_signature)
    EditText etSignature;
    private String Signature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_signature);
        ButterKnife.bind(this);
        etSignature.setText(UserUtils.user.getSign());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            sign_finish("Not_change");
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void sign_finish(String value) {
        Intent intent = new Intent();
        intent.putExtra("result", value);
        setResult(100, intent);
        finish();
    }

    @OnClick({R.id.signature_return, R.id.signature_finsh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signature_return:
                sign_finish("Not_change");
                break;
            case R.id.signature_finsh:
                Signature = etSignature.getText().toString();
                UserUtils.user.setSign(Signature);
                sign_finish("sign");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}

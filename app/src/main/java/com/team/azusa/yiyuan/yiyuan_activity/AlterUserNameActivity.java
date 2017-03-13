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

public class AlterUserNameActivity extends AppCompatActivity {

    @Bind(R.id.name_ET)
    EditText nameET;
    private String name;
    private Intent intent = new Intent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alter_user_name);
        ButterKnife.bind(this);
        nameET.setText(UserUtils.user.getName());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            name_finish("Not_change");
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void name_finish(String value) {
        intent.putExtra("result", value);
        setResult(100, intent);
        finish();
    }

    @OnClick({R.id.name_return, R.id.name_finsh})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.name_return:
                name_finish("Not_change");
                break;
            case R.id.name_finsh:
                name = nameET.getText().toString();
                UserUtils.user.setName(name);
                name_finish("name");
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }
}

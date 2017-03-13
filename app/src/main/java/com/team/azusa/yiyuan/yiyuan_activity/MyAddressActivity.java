package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.AddressLvAdapter;
import com.team.azusa.yiyuan.bean.AddressMessage;
import com.team.azusa.yiyuan.callback.MyAddressCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class MyAddressActivity extends AppCompatActivity {

    @Bind(R.id.lv_address)
    ListView lvAddress;
    @Bind(R.id.address_nodata)
    RelativeLayout rl_addressNodata;
    @Bind(R.id._sure)
    Button sure;

    private ArrayList<AddressMessage> datas = new ArrayList<>();
    private AddressLvAdapter adapter;
    private MyDialog myDialog;
    private AlertDialog loding_dialog;
    private static final int Add_New_Address = 0x1010;
    private static final int Edit_My_Address = 0x2020;
    private int edit_position = 888;//用来标记当前编辑的地址条目
    private int from_what = -1;//标志从onActivityResult启动
    private boolean cancelrequest = false; //是否取消网络请求

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        ButterKnife.bind(this);
        from_what = getIntent().getIntExtra("what", -1);
        if (from_what != -1) {
            sure.setVisibility(View.VISIBLE);
        }
        adapter = new AddressLvAdapter(ConstanceUtils.CONTEXT, datas);
        lvAddress.setAdapter(adapter);
        lvAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edit_position = position;
                Intent intent = new Intent(MyAddressActivity.this, NewAddressActivity.class);
                intent.putExtra("address", JsonUtils.getJsonStringformat(datas.get(position)));
                startActivityForResult(intent, Edit_My_Address);
                adapter.closeAllItems();
            }
        });
        myDialog = new MyDialog();
        loding_dialog = myDialog.showLodingDialog(this);
        initDatas();
    }

    private void initDatas() {
        OkHttpUtils.get().tag("MyAddressActivity")
                .url(Config.IP + "/yiyuan/user_getUserAddress")
                .addParams("firstResult", "0")
                .addParams("userId", UserUtils.user.getId())
                .tag("MyAddressActivity")
                .build().execute(new MyAddressCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelrequest) {
                    return;
                }
                MyToast.showToast("网络连接出错");
                loding_dialog.dismiss();
            }

            @Override
            public void onResponse(ArrayList<AddressMessage> response) {
                datas.addAll(response);
                loding_dialog.dismiss();
                initViews();
            }
        });
    }

    private void initViews() {
        if (datas.size() > 0) {
            rl_addressNodata.setVisibility(View.GONE);
        } else {
            rl_addressNodata.setVisibility(View.VISIBLE);
        }
        lvAddress.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }


    @OnClick({R.id.return_myrecord, R.id.bt_add_address, R.id._sure})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_myrecord:
                MyAddressActivity.this.finish();
                break;
            case R.id.bt_add_address:
                Intent intent = new Intent(MyAddressActivity.this, NewAddressActivity.class);
                startActivityForResult(intent, Add_New_Address);
                break;
            case R.id._sure:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || "".equals(data)) {
            return;
        }
        String Jsonaddress = data.getExtras().getString("address");
        AddressMessage addressMessage = JsonUtils.getObjectfromString(Jsonaddress, AddressMessage.class);
        if (addressMessage.getDefaults().equals("1")) {
            for (int i = 0; i < datas.size(); i++) {
                datas.get(i).setDefaults("0");
            }
        }
        switch (requestCode) {
            case Add_New_Address:
                if (resultCode == 100) {
                    datas.add(addressMessage);
                    rl_addressNodata.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
                break;
            case Edit_My_Address:
                if (resultCode == 100) {
                    datas.set(edit_position, addressMessage);
                    adapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        cancelrequest = true;
        ButterKnife.unbind(this);
        OkHttpUtils.getInstance().cancelTag("MyAddressActivity");
        super.onDestroy();
    }
}

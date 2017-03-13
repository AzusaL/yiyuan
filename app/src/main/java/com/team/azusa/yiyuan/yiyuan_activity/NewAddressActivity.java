package com.team.azusa.yiyuan.yiyuan_activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.AddressSelectAdapter;
import com.team.azusa.yiyuan.bean.AddressMessage;
import com.team.azusa.yiyuan.bean.ProvCityAreaStreet;
import com.team.azusa.yiyuan.callback.NewCityAreaCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.EditTextShakeHelper;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.utils.UserUtils;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewAddressActivity extends AppCompatActivity {

    @Bind(R.id.et_address_name)
    EditText etAddressName;
    @Bind(R.id.et_address_tel)
    EditText etAddressTel;
    @Bind(R.id.et_address_moblie)
    EditText etAddressMoblie;
    @Bind(R.id.et_address_area)
    TextView etAddressArea;
    @Bind(R.id.et_address_street)
    TextView txAddressStreet;
    @Bind(R.id.et_address_detial)
    EditText etAddressDetial;
    @Bind(R.id.et_address_mailcode)
    EditText etAddressMailcode;
    @Bind(R.id.cb_address_default)
    CheckBox cbAddressDefault;

    private String name = "";
    private String tel = "";
    private String moblie = "";
    private String area = "";
    private String street = "";
    private String detial = "";
    private String mailcode = "";
    private String defaults = "";
    private Pattern p;
    private Matcher m;
    private int address_time = 0;
    private AddressSelectAdapter adapter;
    private List<ProvCityAreaStreet> address;
    private AlertDialog dialog;
    private ListView list;
    private AddressMessage addressMessage;
    private boolean isadd = true; //Add address or Edit address
    private boolean cancelrequest = false; //是否取消网络请求

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_address);
        ButterKnife.bind(this);
        String addressMessageStr = getIntent().getStringExtra("address");
        if (!StringUtil.isEmpty(addressMessageStr)) {
            isadd = false;
            addressMessage = JsonUtils.getObjectfromString(addressMessageStr, AddressMessage.class);
            initViews();
        } else {
            isadd = true;
            addressMessage = new AddressMessage();
        }
        initAddress();
    }

    private void initViews() {
        etAddressName.setText(addressMessage.getName());
        etAddressTel.setText(addressMessage.getTelephone());
        etAddressMoblie.setText(addressMessage.getMobile());

        String rough = addressMessage.getRough();
        int i = addressMessage.getRough().lastIndexOf(" ");
        String area = rough.substring(0, i - 1);
        String street = rough.substring(i + 1);

        etAddressArea.setText(area);
        txAddressStreet.setText(street);
        etAddressDetial.setText(addressMessage.getDatail());
        etAddressMailcode.setText(addressMessage.getPostcode());

        cbAddressDefault.setChecked("1".equals(addressMessage.getDefaults()));


    }

    private void initAddress() {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/user_getProCityAreaStreet?parentId=0")
                .tag("NewAddressActivity")
                .build().execute(new NewCityAreaCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(ArrayList<ProvCityAreaStreet> response) {
                address = response;
            }
        });
    }

    private void updateAddress(final int parentId) {
        OkHttpUtils.get().tag("NewAddressActivity").url(Config.IP + "/yiyuan/user_getProCityAreaStreet?parentId=" + parentId)
                .tag("NewAddressActivity")
                .build().execute(new NewCityAreaCallback() {
            @Override
            public void onError(Request request, Exception e) {
            }


            @Override
            public void onResponse(ArrayList<ProvCityAreaStreet> response) {
                if (null != response && response.size() != 0) {
                    address = response;
                    adapter = new AddressSelectAdapter(ConstanceUtils.CONTEXT, address);
                    list.setAdapter(adapter);
                    dialog.show();
                } else {
                    address_time = 0;
                }
            }
        });
    }

    private void httpAdd_And_Save_Address() {
        OkHttpUtils.post().tag("NewAddressActivity")
                .url(Config.IP + "/yiyuan/user_addUserAddress")
                .addParams("address", JsonUtils.getJsonStringformat(addressMessage))
                .tag("NewAddressActivity")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelrequest) {
                    return;
                }
                MyToast.showToast("添加出错");
            }

            @Override
            public void onResponse(String response) {
                MyToast.showToast(response);
                String Jsonaddress = JsonUtils.getJsonStringformat(addressMessage);
                Intent intent = new Intent();
                intent.putExtra("address", Jsonaddress);
                setResult(100, intent);
                NewAddressActivity.this.finish();
            }
        });
    }

    private void httpEdit_And_Save_address(final AddressMessage address) {
        OkHttpUtils.post().tag("NewAddressActivity")
                .url(Config.IP + "/yiyuan/user_modifyAddress")
                .addParams("address", JsonUtils.getJsonStringformat(address))
                .tag("NewAddressActivity")
                .build().execute(new StringCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelrequest) {
                    return;
                }
                MyToast.showToast("修改出错");
            }

            @Override
            public void onResponse(String response) {
                MyToast.showToast("添加成功");
                String Jsonaddress = JsonUtils.getJsonStringformat(address);
                Intent intent = new Intent();
                intent.putExtra("address", Jsonaddress);
                setResult(100, intent);
                NewAddressActivity.this.finish();
            }
        });
    }

    @OnClick({R.id.return_login, R.id.bt_save_address, R.id.et_address_area})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.return_login:
                NewAddressActivity.this.finish();
                break;
            case R.id.bt_save_address:
                name = etAddressName.getText().toString();
                p = Pattern.compile("[\\u4e00-\\u9fa5]{2,}");
                m = p.matcher(name);
                if (!m.matches() || name.length() < 2) {
                    MyToast.showToast_center(getResources().getString(R.string.new_name_verify_fail));
                    new EditTextShakeHelper(getApplicationContext()).shake(etAddressName);
                    return;
                }

                tel = etAddressTel.getText().toString();
                p = Pattern.compile("\\d{3}-?\\d{8}|\\d{4}-?\\d{7}");
                m = p.matcher(tel);
                if (!m.matches() && tel.length() > 0) {
                    MyToast.showToast_center("请输入正确的电话号码，3-4位区号，7-8位电话号码，且只能包含“-”和数字");
                    new EditTextShakeHelper(getApplicationContext()).shake(etAddressTel);
                    return;
                }

                moblie = etAddressMoblie.getText().toString();
                p = Pattern.compile("^1(3[0-9]|5[0-35-9]|8[025-9])\\d{8}$");
                m = p.matcher(moblie);
                if (!m.matches() || moblie.length() < 11) {
                    MyToast.showToast_center("您输入的手机号码不正确");
                    new EditTextShakeHelper(getApplicationContext()).shake(etAddressMoblie);
                    return;
                }

                area = etAddressArea.getText().toString();
                street = txAddressStreet.getText().toString();
                detial = etAddressDetial.getText().toString();
                if (StringUtil.isEmpty(area) || StringUtil.isEmpty(detial) || address_time != 0) {
                    MyToast.showToast_center("您输入的地址信息不完整");
                    new EditTextShakeHelper(getApplicationContext()).shake(etAddressArea, txAddressStreet, etAddressDetial);
                    return;
                }

                mailcode = etAddressMailcode.getText().toString();
                defaults = cbAddressDefault.isChecked() ? "1" : "0";
                addressMessage.setName(name);
                addressMessage.setTelephone(tel);
                addressMessage.setMobile(moblie);
                addressMessage.setRough(area + " " + street);
                addressMessage.setDatail(detial);
                addressMessage.setPostcode(mailcode);
                addressMessage.setDefaults(defaults);
                addressMessage.setUser(UserUtils.user);

                if (isadd) {
                    httpAdd_And_Save_Address();
                } else {
                    httpEdit_And_Save_address(addressMessage);
                }
                break;


            case R.id.et_address_area:
                View view_dialog = View.inflate(ConstanceUtils.CONTEXT, R.layout.address_dialog, null);
                dialog = new MyDialog().showAddrDialog(view_dialog, NewAddressActivity.this);
                dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            txAddressStreet.setText("");
                            dialog.dismiss();
                        }
                        return false;
                    }
                });
                list = (ListView) view_dialog.findViewById(R.id.lv_choose_addr);
                final TextView tv = (TextView) view_dialog.findViewById(R.id.dialog_choose_addr_title);
                tv.setText("请选择省份");
                adapter = new AddressSelectAdapter(ConstanceUtils.CONTEXT, address);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        dialog.dismiss();
                        switch (address_time) {
                            case 0:
                                etAddressArea.setText(address.get(position).getName());
                                tv.setText("请选择市");//下一个dialog的标题
                                break;
                            case 1:
                                tv.setText("请选择区/县");
                                etAddressArea.append(" " + address.get(position).getName());
                                break;
                            case 2:
                                tv.setText("请选择街道");
                                etAddressArea.append(" " + address.get(position).getName());
                                break;
                            case 3:
                                tv.setText("请选择省份");
                                txAddressStreet.setText(address.get(position).getName());
                                break;
                        }
                        address_time++;
                        if (address_time == 4) {
                            address_time = 0;
                            dialog.dismiss();
                            initAddress();
                        } else {
                            updateAddress(address.get(position).getCode());
                        }
                    }
                });
                break;
        }
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        cancelrequest = true;
        OkHttpUtils.getInstance().cancelTag("NewAddressActivity");
        super.onDestroy();
    }
}

package com.team.azusa.yiyuan.adapter;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.AddressMessage;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.utils.JsonUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Delete_exe on 2016/1/18.
 */
public class AddressLvAdapter extends BaseSwipeAdapter {
    private Context context;
    private ArrayList<AddressMessage> datas;
    private ViewHolder holder;

    public AddressLvAdapter(Context context, ArrayList<AddressMessage> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.example;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        final View view = View.inflate(context, R.layout.address_lv_item, null);
        holder = new ViewHolder(view);
        view.setTag(holder);
        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
                view.setEnabled(false);
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                view.setEnabled(true);
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                view.setEnabled(false);
            }

            @Override
            public void onClose(SwipeLayout layout) {
                view.setEnabled(true);
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });
        return view;
    }

    @Override
    public void fillValues(final int position, View convertView) {
        View.OnClickListener clicklistener = new View.OnClickListener() {
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.bt_default_addr:
                        datas.get(position).setDefaults("1");
                        OkHttpUtils.post()
                                .url(Config.IP + "/yiyuan/user_modifyAddress")
                                .addParams("address", JsonUtils.getJsonStringformat(datas.get(position)))
                                .build().execute(new StringCallback() {
                            @Override
                            public void onError(Request request, Exception e) {
                                MyToast.showToast("设置出错");
                            }

                            @Override
                            public void onResponse(String response) {
                                holder.swipeLayout.toggle(false);
                                for (int i = 0; i < datas.size(); i++) {
                                    datas.get(i).setDefaults("0");
                                }
                                datas.get(position).setDefaults("1");
                                notifyDataSetChanged();
                            }
                        });

                        break;
                    case R.id.bt_delete_addr:
                        OkHttpUtils.get()
                                .url(Config.IP + "/yiyuan/user_delUserAddress")
                                .addParams("id", datas.get(position).getId())
                                .build().execute(new StringCallback() {
                            @Override
                            public void onError(Request request, Exception e) {
                                MyToast.showToast("删除出错");
                            }

                            @Override
                            public void onResponse(String response) {
                                MyToast.showToast(response);
                                holder.swipeLayout.toggle(false);
                                datas.remove(position);
                                notifyDataSetChanged();
                            }
                        });
                        break;
                }
            }
        };
        holder = (ViewHolder) convertView.getTag();
        holder.tvAddressUserInfo.setText(datas.get(position).getName() + " " + datas.get(position).getMobile());
        holder.tvLvAddress.setText(datas.get(position).getRough() + " " + datas.get(position).getDatail());
        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);

        holder.btDeleteAddr.setOnClickListener(clicklistener);
        holder.btDefaultAddr.setOnClickListener(clicklistener);
        if (datas.get(position).getDefaults().equals("1")) {
            holder.imgaddressselect.setVisibility(View.VISIBLE);
        } else {
            holder.imgaddressselect.setVisibility(View.GONE);
        }
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

class ViewHolder {
    @Bind(R.id.bt_default_addr)
    Button btDefaultAddr;
    @Bind(R.id.bt_delete_addr)
    ImageButton btDeleteAddr;
    @Bind(R.id.tv_lv_address)
    TextView tvLvAddress;
    @Bind(R.id.tv_address_userinfo)
    TextView tvAddressUserInfo;
    @Bind(R.id.example)
    SwipeLayout swipeLayout;
    @Bind(R.id.img_address_select)
    ImageView imgaddressselect;

    ViewHolder(View view) {
        ButterKnife.bind(this, view);
    }
}
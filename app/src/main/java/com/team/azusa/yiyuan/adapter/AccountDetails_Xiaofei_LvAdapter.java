package com.team.azusa.yiyuan.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.AccountDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Delete_exe on 2016/3/22.
 */
public class AccountDetails_Xiaofei_LvAdapter extends BaseAdapter {
    private List<AccountDetail> datas = null;
    private Context context;

    public AccountDetails_Xiaofei_LvAdapter(List<AccountDetail> datas, Context context) {
        this.datas = datas;
        this.context = context;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.account_details_xiaofei_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.timeXiaofei.setText(datas.get(position).getTime().toString());
        holder.moneyAmount.setText(datas.get(position).getMoney());

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.time_xiaofei)
        TextView timeXiaofei;
        @Bind(R.id.money_xiaofei_amount)
        TextView moneyAmount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

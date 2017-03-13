package com.team.azusa.yiyuan.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.AccountDetail;
import com.team.azusa.yiyuan.utils.DateUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Delete_exe on 2016/3/22.
 */
public class AccountDetails_Chongzhi_LvAdapter extends BaseAdapter {
    private List<AccountDetail> datas = null;
    private Context context;
    private ViewHolder holder;

    public AccountDetails_Chongzhi_LvAdapter(List<AccountDetail> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    public void setDatas(List<AccountDetail> datas) {
        this.datas = datas;
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
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.account_details_chongzhi_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.timeChongzhi.setText(DateUtil.getStringByFormat(datas.get(position).getTime(), DateUtil.dateFormatYMDHMS));
        holder.moneyChannel.setText(datas.get(position).getQudao());
        holder.moneyAmount.setText("￥" + datas.get(position).getMoney());

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.time_chongzhi)
        TextView timeChongzhi;
        @Bind(R.id.money_channel)
        TextView moneyChannel;
        @Bind(R.id.money_chongzhi_amount)
        TextView moneyAmount;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

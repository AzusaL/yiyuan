package com.team.azusa.yiyuan.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.CalculateDto;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.DateUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Azusa on 2016/3/31.
 */
public class CalculateDetailsLvAdapter extends BaseAdapter {

    private ArrayList<CalculateDto> datas;

    public CalculateDetailsLvAdapter(ArrayList<CalculateDto> datas) {
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
        ViewHolder holder;
        if (null == convertView) {
            convertView = View.inflate(ConstanceUtils.CONTEXT, R.layout.item_calculate_lv, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position == datas.size() - 1) {
            holder.viewFgx.setVisibility(View.GONE);
            holder.viewTriangle.setVisibility(View.VISIBLE);
        } else {
            holder.viewFgx.setVisibility(View.VISIBLE);
            holder.viewTriangle.setVisibility(View.GONE);
        }

        //设置时间年月日时分秒毫秒
        holder.tvTimehead.setText(DateUtil.getStringByFormat(datas.get(position).getTime(), "yyyy-MM-dd HH:mm:ss.SSS"));
        //设置时间long类型的数字
        holder.tvTimenum.setText(String.valueOf(datas.get(position).getTime()));
        //设置用户名
        holder.tvUsername.setText(datas.get(position).getUserName());

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_timehead)
        TextView tvTimehead;
        @Bind(R.id.tv_timenum)
        TextView tvTimenum;
        @Bind(R.id.view_triangle)
        View viewTriangle;
        @Bind(R.id.tv_username)
        TextView tvUsername;
        @Bind(R.id.view_fgx)
        View viewFgx;
        @Bind(R.id.rl_jiexiao)
        RelativeLayout rlJiexiao;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

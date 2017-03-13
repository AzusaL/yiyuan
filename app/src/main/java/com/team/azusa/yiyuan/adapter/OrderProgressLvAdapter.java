package com.team.azusa.yiyuan.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.OrderProgress;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.widget.MyTextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Azusa on 2016/3/22.
 */
public class OrderProgressLvAdapter extends BaseAdapter {
    private ArrayList<OrderProgress> datas;

    public OrderProgressLvAdapter(ArrayList<OrderProgress> datas) {
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
        convertView = View.inflate(ConstanceUtils.CONTEXT, R.layout.orderprogresslv_item, null);
        holder = new ViewHolder(convertView);
        holder.tv_msg.setText("" + datas.get(position).getMessage());
        holder.tv_time.setText(DateUtil.getStringByFormat(datas.get(position).getTime(),DateUtil.dateFormatYMDHMS));

        if (0 == position) {
            holder.view_stepCircular.setBackgroundResource(R.drawable.step_on);
            holder.view_stepOffline.setBackgroundResource(R.drawable.stepline_shap);
            holder.view_stepRaw.setBackgroundResource(R.drawable.raworange);
        } else {
            holder.view_stepCircular.setBackgroundResource(R.drawable.step_off);
            holder.view_stepOffline.setBackgroundColor(0xffdddddd);
            holder.view_stepRaw.setBackgroundResource(R.drawable.rawgray);
        }

        //底部分割线
        if (position == datas.size() - 1) {
            holder.divider.setVisibility(View.GONE);
        } else {
            holder.divider.setVisibility(View.VISIBLE);
        }
        int width = ConstanceUtils.CONTEXT.getResources().getDimensionPixelOffset(R.dimen.line_width);
        int maginleft = ConstanceUtils.CONTEXT.getResources().getDimensionPixelOffset(R.dimen.tv_pay_state_marginTop);
        convertView.measure(0, 0);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width, convertView.getMeasuredHeight());
        layoutParams.setMargins(maginleft, 0, 0, 0);
        holder.view_stepOffline.setLayoutParams(layoutParams);
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.step_offline)
        View view_stepOffline;
        @Bind(R.id.step_circular)
        View view_stepCircular;
        @Bind(R.id.step_raw)
        View view_stepRaw;
        @Bind(R.id.tv_msg)
        MyTextView tv_msg;
        @Bind(R.id.tv_optime)
        TextView tv_time;
        @Bind(R.id.view_divider)
        View divider;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

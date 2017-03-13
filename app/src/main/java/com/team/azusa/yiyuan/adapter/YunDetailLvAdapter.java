package com.team.azusa.yiyuan.adapter;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.RecordDto;
import com.team.azusa.yiyuan.listener.MyOnClickListener;
import com.team.azusa.yiyuan.listener.OnLoadListener;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.StringUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Azusa on 2016/3/20.
 */
public class YunDetailLvAdapter extends BaseAdapter {
    private ArrayList<RecordDto> datas;
    private MyOnClickListener onClickListener;

    public void setOnClickListener(MyOnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public YunDetailLvAdapter(ArrayList<RecordDto> datas) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = View.inflate(ConstanceUtils.CONTEXT, R.layout.yundetail_lv_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //设置购买时间和人次
        String time = DateUtil.getStringByFormat(datas.get(position).getTime(), DateUtil.dateFormatYMDHMSSSS);
        holder.tvPlaycount.setText(time + " " + datas.get(position).getBuyNum() + "人次");
        //设置此次购买的云购码
        ArrayList<String> yunnums = StringUtil.getList(datas.get(position).getYunCode());

        //最多只显示两行，即8个云够码
        for (int i = 0; i < 8; i++) {
            if (i < yunnums.size()) {
                holder.textViews.get(i).setVisibility(View.VISIBLE);
                holder.textViews.get(i).setText(yunnums.get(i));
            } else {
                holder.textViews.get(i).setVisibility(View.GONE);
            }
        }
        //云够码个数多于8个时就显示可以查看更多的布局
        if (yunnums.size() > 8) {
            holder.moreyunnumRl.setVisibility(View.VISIBLE);
        } else {
            holder.moreyunnumRl.setVisibility(View.GONE);
        }

        holder.moreyunnumRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener != null) {
                    onClickListener.onClick(position);
                }
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_playcount)
        TextView tvPlaycount;
        @Bind(R.id.yunnum_gv)
        GridLayout yunnumGv;
        @Bind(R.id.moreyunnum_rl)
        RelativeLayout moreyunnumRl;
        @Bind(R.id.tv1)
        TextView tv1;
        @Bind(R.id.tv2)
        TextView tv2;
        @Bind(R.id.tv3)
        TextView tv3;
        @Bind(R.id.tv4)
        TextView tv4;
        @Bind(R.id.tv5)
        TextView tv5;
        @Bind(R.id.tv6)
        TextView tv6;
        @Bind(R.id.tv7)
        TextView tv7;
        @Bind(R.id.tv8)
        TextView tv8;
        ArrayList<TextView> textViews;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
            textViews = new ArrayList<>();
            textViews.add(tv1);
            textViews.add(tv2);
            textViews.add(tv3);
            textViews.add(tv4);
            textViews.add(tv5);
            textViews.add(tv6);
            textViews.add(tv7);
            textViews.add(tv8);
        }
    }

}

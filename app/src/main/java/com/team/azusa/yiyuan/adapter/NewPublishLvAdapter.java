package com.team.azusa.yiyuan.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.utils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.iwgang.countdownview.CountdownView;

/**
 * Created by Delete_exe on 2016/1/20.
 */
public class NewPublishLvAdapter extends BaseAdapter {
    private ArrayList<ProductDto> datas = null;
    private ArrayList<Long> itemBeginTime = null;
    private Context context;

    public NewPublishLvAdapter(ArrayList<ProductDto> datas, ArrayList<Long> itemBeginTime, Context context) {
        this.datas = datas;
        this.context = context;
        this.itemBeginTime = itemBeginTime;
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
        final ViewHolder holder;
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.fg3_newpublish_lv_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(datas.get(position).getImgUrl(), holder.imgLvNewPublish);
        holder.publish_name.setText(datas.get(position).getTitle());
        holder.newpublishWorth.setText("￥" + datas.get(position).getValue());
        long startTime = 1000L * (long) datas.get(position).getDaojishi()
                - (System.currentTimeMillis() - itemBeginTime.get(position));
        if (startTime > 0L) {
            holder.countdownView.setVisibility(View.VISIBLE);
            holder.countdownTxt.setText("揭晓时间:");
            holder.countdownView.start(startTime);
        } else {
            holder.countdownView.setVisibility(View.GONE);
            holder.countdownTxt.setText("计算完成，请前往已揭晓页面查看");
        }
        holder.countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                cv.setVisibility(View.GONE);
                holder.countdownTxt.setText("计算完成，请前往已揭晓页面查看");
            }
        });
        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.img_lv_newpublish)
        SimpleDraweeView imgLvNewPublish;
        @Bind(R.id.cv_countdownView)
        CountdownView countdownView;
        @Bind(R.id.CountdownTxt)
        TextView countdownTxt;
        @Bind(R.id.new_publish_worth)
        TextView newpublishWorth;
        @Bind(R.id.tv_lv_newpublish_name)
        TextView publish_name;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}

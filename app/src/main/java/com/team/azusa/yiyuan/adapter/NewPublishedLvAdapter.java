package com.team.azusa.yiyuan.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.JieXiaoDto;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.StringUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Delete_exe on 2016/3/17.
 */
public class NewPublishedLvAdapter extends BaseAdapter {
    private ArrayList<JieXiaoDto> datas = null;
    private Context context;

    public NewPublishedLvAdapter(ArrayList<JieXiaoDto> datas, Context context) {
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
            convertView = View.inflate(context, R.layout.fg3_newpublished_lv_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(datas.get(position).getProImgUrl(), holder.imgLvNewPublish);
        ImageLoader.getInstance().displayImage(datas.get(position).getWinnerImgUrl(), holder.winnerHead);
        holder.tvLvNewPublishWinner.setText(datas.get(position).getWinnerName());
        holder.tvLvNewPublishWinTime.setText("揭晓时间: " + DateUtil.getStringByFormat(datas.get(position).getJieXiaoTime()
                , DateUtil.dateFormatYMDHMSSSS));
        holder.txLvNewPublicPeoplenum.setText("本云参与：" + datas.get(position).getJoinNum() + "人次");
        holder.newpublishworth.setText("价值：￥" + datas.get(position).getValue());
        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.img_lv_newpublish)
        SimpleDraweeView imgLvNewPublish;
        @Bind(R.id.tv_lv_newpublish_winner)
        TextView tvLvNewPublishWinner;
        @Bind(R.id.tx_lv_newpublic_peoplenum)
        TextView txLvNewPublicPeoplenum;
        @Bind(R.id.tv_lv_newpublish_winTime)
        TextView tvLvNewPublishWinTime;
        @Bind(R.id.winner_head)
        SimpleDraweeView winnerHead;
        @Bind(R.id.new_publish_worth)
        TextView newpublishworth;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}


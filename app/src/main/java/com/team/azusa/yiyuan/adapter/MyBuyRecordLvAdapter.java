package com.team.azusa.yiyuan.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.BuyRecordInfo;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.UserUtils;

import java.util.ArrayList;

/**
 * Created by Delete_exe on 2016/1/18.
 */
public class MyBuyRecordLvAdapter extends BaseAdapter {
    private ArrayList<BuyRecordInfo> datas;
    private Context context;

    public MyBuyRecordLvAdapter(ArrayList<BuyRecordInfo> datas, Context context) {
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
        ViewHolder holder;
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.user_playrecord_rv_item, null);
            holder = new ViewHolder();
            holder.status0 = (LinearLayout) convertView.findViewById(R.id.status0);
            holder.status2 = (RelativeLayout) convertView.findViewById(R.id.status2);
            holder.img_product = (SimpleDraweeView) convertView.findViewById(R.id.img_rv_playrcord);
            holder.tv_productname = (TextView) convertView.findViewById(R.id.tv_lv_mygainedgoods_product);
            holder.tv_winner = (TextView) convertView.findViewById(R.id.tv_lv_winnername);
            holder.tv_luckcode = (TextView) convertView.findViewById(R.id.tv_luckcode);
            holder.pb = (ProgressBar) convertView.findViewById(R.id.fg2_product_pb);
            holder.tv_joinedcount = (TextView) convertView.findViewById(R.id.fg2_product_joinedcount);
            holder.tv_totalcount = (TextView) convertView.findViewById(R.id.fg2_product_allcount);
            holder.tv_remaincount = (TextView) convertView.findViewById(R.id.fg2_product_remaincount);
            holder.tv_text = (TextView) convertView.findViewById(R.id.tv_lv_mybuyrecord_winTime);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //设置产品图片
        ImageLoader.getInstance().displayImage(datas.get(position).getProImgUrl(), holder.img_product);
        //设置产品名
        holder.tv_productname.setText("(第" + datas.get(position).getYunNum() + "云)" + datas.get(position).getTitle());
        String status = datas.get(position).getStatus();
        if (status.equals("0") || status.equals("1")) {
            holder.status0.setVisibility(View.VISIBLE);
            holder.status2.setVisibility(View.GONE);
            //设置进度条
            float buynum = datas.get(position).getBuyNum();
            float totalnum = datas.get(position).getValue();
            holder.pb.setProgress((int) ((buynum / totalnum) * 100));
            //设置产品已参与人数
            holder.tv_joinedcount.setText("" + datas.get(position).getBuyNum());
            //设置产品总需人数
            holder.tv_totalcount.setText("" + datas.get(position).getValue());
            //设置剩余参与人数
            holder.tv_remaincount.setText("" + (datas.get(position).getValue() - datas.get(position).getBuyNum()));
        } else {
            holder.status0.setVisibility(View.GONE);
            holder.status2.setVisibility(View.VISIBLE);
            //设置获奖者
            holder.tv_winner.setText(datas.get(position).getWinnerName());
            if (datas.get(position).getWinnerId().equals(UserUtils.user.getId())) {
                //设置幸运幸运云购码
                holder.tv_text.setText("幸运云购码: ");
                holder.tv_luckcode.setText(datas.get(position).getWinCode());
                holder.tv_luckcode.setTextColor(0xffff7700);
            } else {
                //设置幸运幸运云购码
                holder.tv_text.setText("揭晓时间: ");
                holder.tv_luckcode.setText(DateUtil.getStringbylong(datas.get(position).getJieXiaoTime()
                        , DateUtil.dateFormatYMDHMS));
                holder.tv_luckcode.setTextColor(0xff989898);
            }
        }
        return convertView;
    }

    class ViewHolder {

        public SimpleDraweeView img_product; //产品图片
        public TextView tv_productname; //产品名

        public LinearLayout status0; //状态0，进行中的布局
        public ProgressBar pb;
        public TextView tv_joinedcount;  //已参加人数
        public TextView tv_totalcount;  //总需人数
        public TextView tv_remaincount;  //剩余参与人数

        public RelativeLayout status2;//状态1，等待开奖的布局
        public TextView tv_winner; //获奖者
        public TextView tv_luckcode; //揭晓时间
        public TextView tv_text; //揭晓时间或者幸运云购码


    }

}

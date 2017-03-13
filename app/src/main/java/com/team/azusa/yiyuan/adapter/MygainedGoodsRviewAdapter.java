package com.team.azusa.yiyuan.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.BuyRecordInfo;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;

import java.util.ArrayList;


/**
 * Created by Azusa on 2015/12/27.
 */
public class MygainedGoodsRviewAdapter extends RecyclerView.Adapter<MygainedGoodsRviewAdapter.ViewHolder> {
    private ArrayList<BuyRecordInfo> datas;
    private boolean isuser; //判断是否是用户自己的获得的商品
    private RecyclerViewItemClickLitener recyclerViewItemClickLitener;

    public void setOnItemClickLitener(RecyclerViewItemClickLitener onItemClickLitener) {
        this.recyclerViewItemClickLitener = onItemClickLitener;
    }

    public MygainedGoodsRviewAdapter(ArrayList<BuyRecordInfo> datas, boolean isuser) {
        this.datas = datas;
        this.isuser = isuser;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.my_gaingoods_lv_item, null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //设置产品图片
        ImageLoader.getInstance().displayImage(datas.get(position).getProImgUrl(),holder.img_product);
        //设置产品名
        holder.tv_productname.setText("(第" + datas.get(position).getYunNum() + "云)" + datas.get(position).getTitle());
        //设置幸运云购码
        holder.tv_winnercode.setText(datas.get(position).getWinCode());
        //设置揭晓时间
        holder.tv_winTime.setText(DateUtil.getStringbylong(datas.get(position).getJieXiaoTime(), DateUtil.dateFormatYMDHMS));

        if (recyclerViewItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewItemClickLitener.onItemClick(v, position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView img_product; //产品图片
        public TextView tv_productname; //产品名
        public TextView tv_winnercode; //幸运云购码（获奖码）
        public TextView tv_winTime; //揭晓时间

        public ViewHolder(View view) {
            super(view);
            img_product = (SimpleDraweeView) view.findViewById(R.id.img_rv_mygaingoods);
            tv_productname = (TextView) view.findViewById(R.id.tv_lv_mygainedgoods_product);
            tv_winnercode = (TextView) view.findViewById(R.id.tv_lv_winnercode);
            tv_winTime = (TextView) view.findViewById(R.id.tv_gainedgoods_time);
        }
    }
}

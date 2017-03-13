package com.team.azusa.yiyuan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;
import com.team.azusa.yiyuan.utils.ImageLoader;

import java.util.ArrayList;

/**
 * Created by Azusa on 2015/12/27.
 */
public class HomePopularityRviewAdapter extends RecyclerView.Adapter<HomePopularityRviewAdapter.ViewHolder> {
    public ArrayList<ProductDto> datas = null;
    private Context context;
    private RecyclerViewItemClickLitener recyclerViewItemClickLitener;

    public void setOnItemClickLitener(RecyclerViewItemClickLitener onItemClickLitener) {
        this.recyclerViewItemClickLitener = onItemClickLitener;
    }

    public HomePopularityRviewAdapter(ArrayList<ProductDto> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.homepopularity_rv_item, null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //设置产品图片
        ImageLoader.getInstance().displayImage(datas.get(position).getImgUrl(), holder.img_product);
        // 设置产品价格
        holder.tv_productvalue.setText("" + datas.get(position).getValue());
        //设置产品已参与人数
        holder.tv_joincount.setText("" + datas.get(position).getBuyNum());
        //设置产品总需人数
        holder.tv_allcount.setText("" + datas.get(position).getTotalNum());
        //设置剩余参与人数
        holder.tv_remaincount.setText("" + (datas.get(position).getTotalNum() - datas.get(position).getBuyNum()));
        //设置参与进度条
        float buynum = datas.get(position).getBuyNum();
        float totalnum = datas.get(position).getTotalNum();
        holder.pb.setProgress((int) ((buynum / totalnum) * 100));

        if (recyclerViewItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewItemClickLitener.onItemClick(v, holder.getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView img_product;
        public TextView tv_productvalue;
        public TextView tv_joincount;
        public TextView tv_allcount;
        public TextView tv_remaincount;
        public ProgressBar pb;

        public ViewHolder(View view) {
            super(view);
            img_product = (SimpleDraweeView) view.findViewById(R.id.fg1_popularty_img);
            tv_productvalue = (TextView) view.findViewById(R.id.fg1_popularty_value);
            tv_joincount = (TextView) view.findViewById(R.id.fg1_popularty_joinedcount);
            tv_allcount = (TextView) view.findViewById(R.id.fg1_popularty_allcount);
            tv_remaincount = (TextView) view.findViewById(R.id.fg1_popularty_remaincount);
            pb = (ProgressBar) view.findViewById(R.id.fg1_popularty_pb);
        }
    }
}

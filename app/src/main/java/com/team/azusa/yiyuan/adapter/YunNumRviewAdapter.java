package com.team.azusa.yiyuan.adapter;

import android.graphics.Color;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;

import java.util.ArrayList;

import cn.iwgang.countdownview.CountdownView;

/**
 * Created by Azusa on 2015/12/27.
 */
public class YunNumRviewAdapter extends RecyclerView.Adapter<YunNumRviewAdapter.ViewHolder> {
    public ArrayList<String> datas = null;
    public int yunNumber;
    public int lastNumber;
    private RecyclerViewItemClickLitener recyclerViewItemClickLitener;

    public void setOnItemClickLitener(RecyclerViewItemClickLitener onItemClickLitener) {
        this.recyclerViewItemClickLitener = onItemClickLitener;
    }

    public YunNumRviewAdapter(ArrayList<String> datas,int yunNumber,int lastNumber) {
        this.datas = datas;
        this.yunNumber = yunNumber;
        this.lastNumber = lastNumber;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.item_whgv, null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_yunnum.setText(datas.get(position));
        if(yunNumber == lastNumber - position){
            holder.tv_yunnum.setTextColor(Color.parseColor("#FF7F24"));
        }else {
            holder.tv_yunnum.setTextColor(Color.parseColor("#404040"));
        }
        if(yunNumber == lastNumber){
            holder.tv_yunnum.setText("第" + (lastNumber-position) + "云");
        }
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
        public TextView tv_yunnum;

        public ViewHolder(View view) {
            super(view);
            tv_yunnum = (TextView) view.findViewById(R.id.gv_txview);
        }
    }
}

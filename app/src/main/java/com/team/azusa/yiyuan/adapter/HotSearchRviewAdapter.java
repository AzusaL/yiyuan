package com.team.azusa.yiyuan.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;

import java.util.ArrayList;

import cn.iwgang.countdownview.CountdownView;

/**
 * Created by Azusa on 2015/12/27.
 */
public class HotSearchRviewAdapter extends RecyclerView.Adapter<HotSearchRviewAdapter.ViewHolder> {
    public ArrayList<String> datas = null;
    private RecyclerViewItemClickLitener recyclerViewItemClickLitener;

    public void setOnItemClickLitener(RecyclerViewItemClickLitener onItemClickLitener) {
        this.recyclerViewItemClickLitener = onItemClickLitener;
    }

    public HotSearchRviewAdapter(ArrayList<String> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.hotsearchrv_item, null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.tv_searchtext.setText(datas.get(position));
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
        public TextView tv_searchtext;

        public ViewHolder(View view) {
            super(view);
            tv_searchtext = (TextView) view.findViewById(R.id.hot_search_tv);
        }
    }
}

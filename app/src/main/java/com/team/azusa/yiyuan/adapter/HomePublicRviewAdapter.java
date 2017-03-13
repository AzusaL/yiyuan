package com.team.azusa.yiyuan.adapter;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;

import java.util.ArrayList;

import cn.iwgang.countdownview.CountdownView;

/**
 * Created by Azusa on 2015/12/27.
 */
public class HomePublicRviewAdapter extends RecyclerView.Adapter<HomePublicRviewAdapter.ViewHolder> {
    public ArrayList<ProductDto> datas = null;
    private RecyclerViewItemClickLitener recyclerViewItemClickLitener;
    private Handler handler;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setOnItemClickLitener(RecyclerViewItemClickLitener onItemClickLitener) {
        this.recyclerViewItemClickLitener = onItemClickLitener;
    }

    public HomePublicRviewAdapter(ArrayList<ProductDto> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.home_rv_newpublic_item, null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (datas.size() < 4) {
            ProductDto dto = new ProductDto();
            for (int i = 0; i < 4 - datas.size(); i++) {
                dto.setDaojishi(10000);
                dto.setImgUrl("http://pic14.nipic.com/20110522/7411759_164157418126_2.jpg");
                datas.add(dto);
            }
        }
        holder.newpublic_tvname.setText(datas.get(position).getTitle());
        holder.countdownView.start(1000L * (long) datas.get(position).getDaojishi());
        holder.countdownView.setOnCountdownEndListener(new CountdownView.OnCountdownEndListener() {
            @Override
            public void onEnd(CountdownView cv) {
                handler.sendEmptyMessage(1);
            }
        });
        holder.simpleDraweeView.setImageURI(Uri.parse(datas.get(position).getImgUrl()));
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
        return 4;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CountdownView countdownView;
        public SimpleDraweeView simpleDraweeView;
        public TextView newpublic_tvname;

        public ViewHolder(View view) {
            super(view);
            newpublic_tvname = (TextView) view.findViewById(R.id.fg1_newpublic_tvname);
            countdownView = (CountdownView) view.findViewById(R.id.cv_countdownView);
            simpleDraweeView = (SimpleDraweeView) view.findViewById(R.id.fg1_newpublic_img);
        }
    }
}

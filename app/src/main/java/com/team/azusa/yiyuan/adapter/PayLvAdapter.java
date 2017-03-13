package com.team.azusa.yiyuan.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.GoodsCar;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Azusa on 2016/3/26.
 */
public class PayLvAdapter extends BaseAdapter {
    private ArrayList<GoodsCar> datas;

    public PayLvAdapter(ArrayList<GoodsCar> datas) {
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
        if(null == convertView){
            convertView = View.inflate(ConstanceUtils.CONTEXT, R.layout.pay_lv_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        //设置商品图片
        ImageLoader.getInstance().displayImage(datas.get(position).getProductDto().getImgUrl(),holder.imgProduct);
        //设置商品名字
        holder.tvProductName.setText(datas.get(position).getProductDto().getTitle());
        //设置人次和价格
        holder.tvCount.setText(datas.get(position).getBuyCount()+"人次/￥"+datas.get(position).getBuyCount()+".00");

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.view)
        View view;
        @Bind(R.id.img_product)
        SimpleDraweeView imgProduct;
        @Bind(R.id.tv_count)
        TextView tvCount;
        @Bind(R.id.tv_product_name)
        TextView tvProductName;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

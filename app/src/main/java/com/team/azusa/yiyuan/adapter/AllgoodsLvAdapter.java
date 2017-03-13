package com.team.azusa.yiyuan.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.utils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by Azusa on 2016/1/15.
 */
public class AllgoodsLvAdapter extends BaseAdapter {
    private ArrayList<ProductDto> datas;
    private Context context;
    private AddCarClickListener mAddCarClickListener;

    public AllgoodsLvAdapter(ArrayList<ProductDto> datas, Context context) {
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.fg2_allgoodslv_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        //设置产品图片
        ImageLoader.getInstance().displayImage(datas.get(position).getImgUrl(), holder.img_product);
        //设置是否限购
        if ("1".equals(datas.get(position).getXianGou())) {
            holder.img_Limibg.setVisibility(View.VISIBLE);
        } else {
            holder.img_Limibg.setVisibility(View.GONE);
        }
        // 设置产品名
        holder.tv_productName.setText("(第" + datas.get(position).getYunNum() + "云)" + datas.get(position).getTitle());
        // 设置产品价格
        holder.tv_productValue.setText("" + datas.get(position).getValue());
        //设置产品已参与人数
        holder.tv_playcount.setText("" + datas.get(position).getBuyNum());
        //设置产品总需人数
        holder.tv_allplaycount.setText("" + datas.get(position).getTotalNum());
        //设置剩余参与人数
        holder.tv_remaincount.setText("" + (datas.get(position).getTotalNum() - datas.get(position).getBuyNum()));
        //设置参与进度条
        float buynum = datas.get(position).getBuyNum();
        float totalnum = datas.get(position).getTotalNum();
        holder.pb.setProgress((int) ((buynum / totalnum) * 100));
        //添加购物车按钮监听
        holder.btn_gouwuche.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddCarClickListener != null) {
                    int[] start_location = new int[2];
                    holder.img_product.getLocationInWindow(start_location);//获取点击商品图片的位置
                    holder.img_product.setDrawingCacheEnabled(false); //清除复制的bitmap
                    holder.img_product.setDrawingCacheEnabled(true); //设置为true才能复制bitmap
                    Bitmap bitmap = holder.img_product.getDrawingCache(); //复制一个bitmap
                    mAddCarClickListener.onAddCarClick(position, bitmap, start_location);
                }
                EventBus.getDefault().post(datas.get(position));
            }
        });
        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.img_lv_allgoods)
        SimpleDraweeView img_product;
        @Bind(R.id.fg2_limibg)
        ImageView img_Limibg;
        @Bind(R.id.fg2_product_name)
        TextView tv_productName;
        @Bind(R.id.fg2_product_value)
        TextView tv_productValue;
        @Bind(R.id.fg2_addtocar)
        Button btn_gouwuche;
        @Bind(R.id.fg2_product_joinedcount)
        TextView tv_playcount;
        @Bind(R.id.fg2_product_allcount)
        TextView tv_allplaycount;
        @Bind(R.id.fg2_product_remaincount)
        TextView tv_remaincount;
        @Bind(R.id.fg2_product_pb)
        ProgressBar pb;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public void SetOnAddCarClickListener(AddCarClickListener ClickListener) {
        this.mAddCarClickListener = ClickListener;
    }

    public interface AddCarClickListener {
        public void onAddCarClick(int position, Bitmap drawable, int[] start_location);
    }
}

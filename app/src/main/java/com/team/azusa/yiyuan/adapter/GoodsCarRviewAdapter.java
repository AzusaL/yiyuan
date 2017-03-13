package com.team.azusa.yiyuan.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.GoodsCar;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.listener.RecyclerViewItemClickLitener;
import com.team.azusa.yiyuan.utils.ImageLoader;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.widget.MyDialog;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by Azusa on 2015/1/25.
 */
public class GoodsCarRviewAdapter extends RecyclerView.Adapter<GoodsCarRviewAdapter.ViewHolder> {
    private ArrayList<GoodsCar> datas = null;
    private Context context;
    private int buy_count;
    private RecyclerViewItemClickLitener recyclerViewItemClickLitener;

    public void setOnItemClickLitener(RecyclerViewItemClickLitener onItemClickLitener) {
        this.recyclerViewItemClickLitener = onItemClickLitener;
    }

    public GoodsCarRviewAdapter(ArrayList<GoodsCar> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.goodscar_rv_item, null);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        ProductDto productDto = datas.get(position).getProductDto();
        //设置产品图片
        ImageLoader.getInstance().displayImage(productDto.getImgUrl(), holder.img_product);
        //设置云期数
        holder.tv_productyunnum.setText("(第" + productDto.getYunNum() + "云)");
        //设置产品名
        holder.tv_productname.setText(productDto.getTitle());
        //设置剩余购买次数和是否限购
        String count = (productDto.getTotalNum() - productDto.getBuyNum()) + "";
        if (productDto.getXianGou().equals("1")) {
            holder.img_limit.setVisibility(View.VISIBLE);
            holder.tv_remaincount.setText("剩余" + count + "人次/限购5人次");
        } else {
            holder.img_limit.setVisibility(View.GONE);
            holder.tv_remaincount.setText("剩余" + count + "人次");
        }
        //设置添加购买次数显示
        holder.btn_joincount.setText(datas.get(position).getBuyCount() + "");
        //设置购买该商品总额
        holder.tv_totalyuan.setText(datas.get(position).getBuyCount() + ".00元");
        //设置添加购买次数按钮监听
        holder.btn_joincount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View dialogview = View.inflate(context, R.layout.addcar_dialog, null);
                AlertDialog dialog = new MyDialog().showAddgoodsCarDialog(context, dialogview);
                setDialogView(holder.getAdapterPosition(), dialog, dialogview);
            }
        });

        //设置删除按钮监听
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
                ProductDto dto = new ProductDto();
                EventBus.getDefault().post(dto);
            }
        });

        //设置item点击监听
        if (recyclerViewItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerViewItemClickLitener.onItemClick(v, position);
                }
            });
        }

    }

    private void setDialogView(final int position, final AlertDialog dialog, View dialogview) {
        final TextView tv_dialog_remian = (TextView) dialogview.findViewById(R.id.tv_dialog_remaincount);
        Button btn_decrease = (Button) dialogview.findViewById(R.id.btn_decrease);
        Button btn_increase = (Button) dialogview.findViewById(R.id.btn_increase);
        final Button btn_cancel = (Button) dialogview.findViewById(R.id.dialog_cancel_btn);
        final Button btn_sure = (Button) dialogview.findViewById(R.id.dialog_surebtn);
        final EditText edt_count = (EditText) dialogview.findViewById(R.id.dialog_edt_count);
        ProductDto productDto = datas.get(position).getProductDto();
        final int remain_count; //剩余购买人次
        //判断是否限购
        if (productDto.getXianGou().equals("1")) {
            remain_count = 5;
        } else {
            remain_count = (productDto.getTotalNum() - productDto.getBuyNum());
        }
        buy_count = datas.get(position).getBuyCount();
        tv_dialog_remian.setText("剩余" + remain_count + "人次");
        edt_count.setText(datas.get(position).getBuyCount() + "");
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btn_decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buy_count > 0) {
                    buy_count--;
                    edt_count.setText(buy_count + "");
                } else {
                    return;
                }
            }
        });

        btn_increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buy_count++;
                edt_count.setText(buy_count + "");
            }
        });

        edt_count.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (StringUtil.isEmpty(s.toString())) {
                    edt_count.setText("0");
                    buy_count = 0;
                    btn_sure.setEnabled(false);
                    btn_sure.setBackgroundResource(R.drawable.gray_shape);
                    return;
                }
                if (s.toString().startsWith("0") && s.toString().length() > 1) {
                    edt_count.setText(s.toString().replace("0", ""));
                }
                if (Integer.valueOf(s.toString()) > remain_count) {
                    tv_dialog_remian.setText("最多可购买" + remain_count + "人次");
                    tv_dialog_remian.setTextColor(Color.parseColor("#ff7700"));
                    edt_count.setText(remain_count + "");
                    buy_count = remain_count;
                    return;
                }
                if (Integer.valueOf(s.toString()) < remain_count) {
                    tv_dialog_remian.setText("剩余" + remain_count + "人次");
                    tv_dialog_remian.setTextColor(Color.parseColor("#555555"));
                }
                buy_count = Integer.valueOf(s.toString());
                if (buy_count == 0) {
                    btn_sure.setEnabled(false);
                    btn_sure.setBackgroundResource(R.drawable.gray_shape);
                } else {
                    btn_sure.setEnabled(true);
                    btn_sure.setBackgroundResource(R.drawable.button_envy);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas.get(position).setBuyCount(buy_count);
                notifyItemChanged(position);
                dialog.dismiss();
                ProductDto dto = new ProductDto();
                dto.setProductId("-1");
                EventBus.getDefault().post(dto);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public SimpleDraweeView img_product; //产品图片
        public ImageView img_limit; //限购图标标示
        public TextView tv_productyunnum; //产品云期数
        public TextView tv_productname; //产品名
        public TextView tv_remaincount; //剩余购买人次
        public TextView tv_totalyuan; //购买总价
        public Button btn_joincount; //添加购买人次按钮
        public RelativeLayout btn_delete; //删除按钮


        public ViewHolder(View view) {
            super(view);
            img_product = (SimpleDraweeView) view.findViewById(R.id.img_rv_car);
            img_limit = (ImageView) view.findViewById(R.id.car_limibg);
            tv_productyunnum = (TextView) view.findViewById(R.id.tv_car_yunnum);
            tv_productname = (TextView) view.findViewById(R.id.tv_cartitle);
            tv_remaincount = (TextView) view.findViewById(R.id.goodscar_renshu);
            btn_joincount = (Button) view.findViewById(R.id.joincount_btn);
            btn_delete = (RelativeLayout) view.findViewById(R.id.goodscar_deletebtn);
            tv_totalyuan = (TextView) view.findViewById(R.id.tv_totalyuan);
        }
    }

}

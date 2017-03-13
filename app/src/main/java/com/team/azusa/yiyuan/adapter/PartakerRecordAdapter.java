package com.team.azusa.yiyuan.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.JoinRecordDto;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.DateUtil;
import com.team.azusa.yiyuan.utils.ImageLoader;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/1/20 0020.
 */
public class PartakerRecordAdapter extends BaseAdapter {

    private ArrayList<JoinRecordDto> datas = null;
    private String blackTextView;
    private SpannableStringBuilder builder;
    private ForegroundColorSpan orangeSpan;
    private int what;

    /**
     *
     * @param datas
     * @param what 区分是显示全站购买记录还是某商品的所有购买记录 1为全站，其他为某商品
     */
    public PartakerRecordAdapter(ArrayList<JoinRecordDto> datas, int what) {
        this.datas = datas;
        this.what = what;
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
        ViewCache cache;
        if (convertView == null) {
            convertView = View.inflate(ConstanceUtils.CONTEXT, R.layout.all_partaker_record_item, null);
            cache = new ViewCache(convertView);
            convertView.setTag(cache);
        } else {
            cache = (ViewCache) convertView.getTag();
        }
        JoinRecordDto joinRecordDto = datas.get(position);
        ImageLoader.getInstance().displayImage(joinRecordDto.getJoinUserImgUrl(), cache.img_all_partaker_record);
        cache.all_partaker_user_name.setText(joinRecordDto.getJoinUserName());
        if (1 == what) {
            cache.tv_partaker_productname.setVisibility(View.VISIBLE);
            cache.tv_partaker_productname.setText("(第" + joinRecordDto.getYunNum() + "云) " + joinRecordDto.getProductTitle());
        } else {
            cache.tv_partaker_productname.setVisibility(View.GONE);
        }
        blackTextView = "参与了";
        builder = new SpannableStringBuilder(joinRecordDto.getBuyNum() + "人次");
        orangeSpan = new ForegroundColorSpan(Color.parseColor("#FF7F24"));
        builder.setSpan(orangeSpan, 0, String.valueOf(joinRecordDto.getBuyNum()).length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        cache.partaker_number.setText(blackTextView);
        cache.partaker_number.append(builder);
        cache.tv_lv_time.setText(DateUtil.getStringByFormat(joinRecordDto.getJoinTime(), "yyyy-MM-dd HH:mm:ss:sss"));
        return convertView;
    }

    static final class ViewCache {
        @Bind(R.id.all_partaker_record)
        SimpleDraweeView img_all_partaker_record;//头像
        @Bind(R.id.all_partaker_user_name)
        TextView all_partaker_user_name;//参与者名字
        @Bind(R.id.tv_partaker_productname)
        TextView tv_partaker_productname;//参与记录所对应的商品名
        @Bind(R.id.partaker_number)
        TextView partaker_number;//参与次数
        @Bind(R.id.tv_lv_time)
        TextView tv_lv_time;

        ViewCache(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

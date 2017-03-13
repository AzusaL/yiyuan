package com.team.azusa.yiyuan.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.bean.ProvCityAreaStreet;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Delete_exe on 2016/3/6.
 */
public class AddressSelectAdapter extends BaseAdapter {
    private Context context;
    private List<ProvCityAreaStreet> datas;

    public AddressSelectAdapter(Context context, List<ProvCityAreaStreet> datas) {
        this.context = context;
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
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.search_fg1lv_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_text.setText(datas.get(position).getName());
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_recentsearchtext)
        TextView tv_text;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}

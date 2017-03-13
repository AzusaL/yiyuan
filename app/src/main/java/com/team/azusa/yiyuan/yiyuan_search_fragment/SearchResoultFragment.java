package com.team.azusa.yiyuan.yiyuan_search_fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.Request;
import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.adapter.AllgoodsLvAdapter;
import com.team.azusa.yiyuan.bean.ProductDto;
import com.team.azusa.yiyuan.callback.SearchResultCallback;
import com.team.azusa.yiyuan.config.Config;
import com.team.azusa.yiyuan.event.AddCarsEvent;
import com.team.azusa.yiyuan.event.IntParameterEvent;
import com.team.azusa.yiyuan.listener.OnLoadListener;
import com.team.azusa.yiyuan.utils.ConstanceUtils;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.widget.AddCarAnimation;
import com.team.azusa.yiyuan.widget.BadgeView;
import com.team.azusa.yiyuan.widget.MyDialog;
import com.team.azusa.yiyuan.widget.PulluptoRefreshListview;
import com.team.azusa.yiyuan.yiyuan_activity.GoodsDetailsActivity;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


public class SearchResoultFragment extends Fragment {

    @Bind(R.id.search_find)
    TextView tv_searchFind;
    @Bind(R.id.search_lv)
    PulluptoRefreshListview mlistview;
    @Bind(R.id.search_floatbtn)
    FrameLayout float_btn;
    @Bind(R.id.car_img)
    ImageView car_img;
    private AlertDialog dialog;
    private AllgoodsLvAdapter adapter;
    private ArrayList<ProductDto> datas;
    private View view;
    private String search_key;
    private int lastX, lastY;
    private BadgeView badge;
    private int car_count;
    private boolean cancelrequest = false; //是否取消网络请求

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (1 == msg.what) {
                float_btn.setClickable(true);
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.search_fg2, null);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initData();
        iniView();
        setListener();
        getData(0, 0);
        return view;
    }

    private void setListener() {
        WindowManager wm = getActivity().getWindowManager();
        final int screenWidth = wm.getDefaultDisplay().getWidth();

        float_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int ev = event.getAction();
                switch (ev) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        if (Math.abs(dx) > 0 || Math.abs(dy) > 0) {
                            float_btn.setClickable(false);
                        }
                        int l = v.getLeft() + dx;
                        int b = v.getBottom() + dy;
                        int r = v.getRight() + dx;
                        int t = v.getTop() + dy;
                        if (l < 0) {
                            l = 0;
                            r = l + v.getWidth();
                        }
                        if (t < 0) {
                            t = 0;
                            b = t + v.getHeight();

                        }
                        if (r > screenWidth) {
                            r = screenWidth;
                            l = r - v.getWidth();
                        }

                        v.layout(l, t, r, b);
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        v.postInvalidate();
                        break;
                    case MotionEvent.ACTION_UP:
                        handler.sendEmptyMessageDelayed(1, 1);
                        break;
                }
                return false;
            }
        });

        adapter.SetOnAddCarClickListener(new AllgoodsLvAdapter.AddCarClickListener() {
            @Override
            public void onAddCarClick(int position, Bitmap drawable, int[] start_location) {
                int[] car_location = new int[2];
                float_btn.getLocationInWindow(car_location);
                new AddCarAnimation(getActivity()).doAnim(drawable, start_location, car_location);
            }
        });

        mlistview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == datas.size()) {
                    return;
                }
                String productId = datas.get(position).getProductId();
                int yunNum = datas.get(position).getYunNum();

                Intent intent = new Intent(getActivity(), GoodsDetailsActivity.class);
                intent.putExtra("productId", productId);
                intent.putExtra("yunNum", yunNum + "");
                startActivity(intent);
            }
        });
    }

    private void iniView() {
        dialog = new MyDialog().showLodingDialog(getActivity());
        mlistview.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onLoad() {
                getData((datas.size() + 1), 1);
            }
        });
        badge = new BadgeView(getActivity());
        badge.setTargetView(car_img);
        badge.setBackgroundResource(R.drawable.cart_count_bg);
        badge.setBadgeCount(car_count);
        badge.setBadgeMargin(0, 10, 10, 0);
        badge.setPadding(2, 2, 2, 2);
    }

    private void initData() {
        datas = new ArrayList<>();
        adapter = new AllgoodsLvAdapter(datas, ConstanceUtils.CONTEXT);
        mlistview.setAdapter(adapter);

        car_count = ConstanceUtils.carcount;
        search_key = getArguments().getString("search_key");

    }

    //eventbus接收购物车页面发来的添加个数
    public void onEventMainThread(AddCarsEvent event) {
        if (0 == event.getCount()) {
            car_count = 0;
        } else {
            car_count = car_count + event.getCount();
        }
        ConstanceUtils.carcount = car_count;
        badge.setBadgeCount(car_count);
    }

    /**
     * 从服务器获取数据
     *
     * @param firstResult 要加载的第一条数据的position
     */
    private void getData(final int firstResult, final int what) {
        OkHttpUtils.get().url(Config.IP + "/yiyuan/b_getYunNumsByKey")
                .addParams("keyWords", search_key)
                .addParams("firstResult", firstResult + "")
                .tag("SearchResoultFragment")
                .build().execute(new SearchResultCallback() {
            @Override
            public void onError(Request request, Exception e) {
                if (cancelrequest) {
                    return;
                }
                MyToast.showToast("网络连接出错");
            }

            @Override
            public void onBefore(Request request) {
                Log.e("main", request.urlString());
                super.onBefore(request);
            }

            @Override
            public void onResponse(HashMap<String, Object> response) {
                if ("xianGou".equals(search_key)) {
                    search_key = "限购";
                }
                ArrayList<ProductDto> product_response = (ArrayList<ProductDto>) response.get("products");
                //product_response为空表示没有更多数据了
                if (product_response == null || product_response.isEmpty()) {
                    if (0 == what) {
                        SpannableString string = new SpannableString("找到 " + 0 + " 个关于“ " + search_key + " ”的商品");
                        string.setSpan(new ForegroundColorSpan(Color.parseColor("#ff7700")), 3, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        string.setSpan(new ForegroundColorSpan(Color.parseColor("#ff7700")), 10, 10 + search_key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_searchFind.setText(string);
                        dialog.dismiss();
                    } else {
                        mlistview.setLoadComplete(true);
                    }
                    return;
                } else if (0 == what) {
                    datas.clear(); // 如果respomse不为空且what为0，表示刷新数据，先清空数据集合
                }
                mlistview.setLoading(false);
                datas.addAll(product_response);
                if (0 == what) {
                    String i = response.get("count") + "";
                    SpannableString string = new SpannableString("找到 " + i + " 个关于“ " + search_key + " ”的商品");
                    string.setSpan(new ForegroundColorSpan(Color.parseColor("#ff7700")), 3, i.length() + 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    string.setSpan(new ForegroundColorSpan(Color.parseColor("#ff7700")), i.length() + 8, i.length() + 9 + search_key.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    tv_searchFind.setText(string);
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelrequest = true;
        OkHttpUtils.getInstance().cancelTag("SearchResoultFragment");
        ButterKnife.unbind(this);
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.search_floatbtn)
    public void onClick() {
        EventBus.getDefault().post(new IntParameterEvent(3));
        getActivity().finish();
    }

}

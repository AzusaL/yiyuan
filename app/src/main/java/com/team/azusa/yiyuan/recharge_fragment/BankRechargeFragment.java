package com.team.azusa.yiyuan.recharge_fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.team.azusa.yiyuan.R;
import com.team.azusa.yiyuan.utils.MyToast;
import com.team.azusa.yiyuan.utils.StringUtil;
import com.team.azusa.yiyuan.utils.UserUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class BankRechargeFragment extends Fragment {

    @Bind(R.id.tv_yuer)
    TextView mTvYuer;
    @Bind(R.id.radio50)
    RadioButton mRadio50;
    @Bind(R.id.radio100)
    RadioButton mRadio100;
    @Bind(R.id.radio200)
    RadioButton mRadio200;
    @Bind(R.id.radio500)
    RadioButton mRadio500;
    @Bind(R.id.radio1000)
    RadioButton mRadio1000;
    @Bind(R.id.edit_recharge)
    EditText mEditRecharge;
    @Bind(R.id.cb_weichatpay)
    CheckBox mCbWeichatpay;
    @Bind(R.id.rl_weichat_pay)
    RelativeLayout mRlWeichatPay;
    @Bind(R.id.cb_alipay)
    CheckBox mCbAlipay;
    @Bind(R.id.rl_ali_pay)
    RelativeLayout mRlAliPay;
    @Bind(R.id.btn_recharge)
    Button mBtnRecharge;
    private View view;
    private ArrayList<RadioButton> mRadioButtons = new ArrayList<>(); //充值金额选取的btn
    private int rechargecount = 50; //充值金额

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recharge_fg1_bank, null);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mRadioButtons.add(mRadio50);
        mRadioButtons.add(mRadio100);
        mRadioButtons.add(mRadio200);
        mRadioButtons.add(mRadio500);
        mRadioButtons.add(mRadio1000);

        //设置余额
        mTvYuer.setText(UserUtils.user.getBalance() + ".00");
        //默认使用微信支付
        mCbWeichatpay.setChecked(true);
    }

    //更改按钮背景
    private void setBackground(int checkposition) {
        for (int i = 0; i < mRadioButtons.size(); i++) {
            if (i == checkposition) {
                mRadioButtons.get(i).setBackgroundResource(R.drawable.radio_on);
                mRadioButtons.get(i).setTextColor(0xffff6600);
            } else {
                mRadioButtons.get(i).setBackgroundResource(R.drawable.radio_off);
                mRadioButtons.get(i).setTextColor(0xff555555);
            }
        }
        if (checkposition != 5) {
            mEditRecharge.setBackgroundResource(R.drawable.radio_off);
            mEditRecharge.setText("");
            //点击别的金额按钮后隐藏键盘
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(),
                        0);
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.edit_recharge, R.id.radio50, R.id.radio100, R.id.radio200, R.id.radio500, R.id.radio1000, R.id.rl_weichat_pay, R.id.rl_ali_pay, R.id.btn_recharge})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.radio50:
                rechargecount = 50;
                setBackground(0);
                break;
            case R.id.radio100:
                rechargecount = 100;
                setBackground(1);
                break;
            case R.id.radio200:
                rechargecount = 200;
                setBackground(2);
                break;
            case R.id.radio500:
                rechargecount = 500;
                setBackground(3);
                break;
            case R.id.radio1000:
                rechargecount = 1000;
                setBackground(4);
                break;
            case R.id.edit_recharge:
                rechargecount = 0;
                setBackground(5);
                mEditRecharge.setBackgroundResource(R.drawable.radio_on);
                break;
            case R.id.rl_weichat_pay:
                mCbWeichatpay.setChecked(true);
                mCbAlipay.setChecked(false);
                break;
            case R.id.rl_ali_pay:
                mCbWeichatpay.setChecked(false);
                mCbAlipay.setChecked(true);
                break;
            case R.id.btn_recharge:
                if (rechargecount == 0 && StringUtil.isEmpty(mEditRecharge.getText().toString())) {
                    MyToast.showToast_center("您还没有输入金额");
                }
                break;
        }
    }

}

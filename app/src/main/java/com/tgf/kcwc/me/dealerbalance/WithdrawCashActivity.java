package com.tgf.kcwc.me.dealerbalance;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tgf.kcwc.R;
import com.tgf.kcwc.base.BaseActivity;
import com.tgf.kcwc.common.Constants;
import com.tgf.kcwc.mvp.model.AccountBalanceModel;
import com.tgf.kcwc.mvp.model.BankCardModel;
import com.tgf.kcwc.mvp.model.ResponseMessage;
import com.tgf.kcwc.mvp.presenter.DealerWalletPresenter;
import com.tgf.kcwc.mvp.view.DealerWalletView;
import com.tgf.kcwc.util.ArithmeticUtil;
import com.tgf.kcwc.util.CommonUtils;
import com.tgf.kcwc.util.EncryptUtil;
import com.tgf.kcwc.util.IOUtils;
import com.tgf.kcwc.util.TextUtil;
import com.tgf.kcwc.view.DealerPayPwdValidatePopupWindow;
import com.tgf.kcwc.view.FunctionView;

import java.util.HashMap;
import java.util.Map;

/**
 * Author:Jenny
 * Date:2017/10/16
 * E-mail:fishloveqin@gmail.com
 * <p>
 * 提现
 */

public class WithdrawCashActivity extends BaseActivity implements DealerWalletView<BankCardModel> {
    protected ImageView bankCardImg;
    protected TextView bankName;
    protected RelativeLayout addBankCardLayout;
    protected TextView moneyTagTv;
    protected TextView balanceTv;
    protected TextView payRemarkTv;
    protected Button confirmBtn;
    protected EditText cashET;
    protected RelativeLayout addBankLayout1;
    protected ImageView bankCardImg2;
    protected TextView bankName2;
    protected TextView cardInfoTv;
    protected RelativeLayout addBankLayout2;
    private DealerWalletPresenter mPresenter, mApplyPresenter, mBalancePresenter;
    private BankCardModel mModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_cash);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.getSelectedBankCard(IOUtils.getToken(mContext));
    }

    @Override
    protected void setUpViews() {
        initView();
    }

    @Override
    protected void titleBarCallback(ImageButton back, FunctionView function, TextView text) {

        backEvent(back);
        text.setText("提现");
    }

    private void initView() {
        bankCardImg = (ImageView) findViewById(R.id.bankCardImg);
        bankName = (TextView) findViewById(R.id.bankName);
        addBankCardLayout = (RelativeLayout) findViewById(R.id.addBankCardLayout);
        moneyTagTv = (TextView) findViewById(R.id.moneyTagTv);
        balanceTv = (TextView) findViewById(R.id.balanceTv);
        payRemarkTv = (TextView) findViewById(R.id.payRemarkTv);
        confirmBtn = (Button) findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(this);
        cashET = (EditText) findViewById(R.id.cashET);

        addBankLayout1 = (RelativeLayout) findViewById(R.id.addBankLayout1);
        addBankLayout1.setOnClickListener(this);
        bankCardImg2 = (ImageView) findViewById(R.id.bankCardImg2);
        bankName2 = (TextView) findViewById(R.id.bankName2);
        cardInfoTv = (TextView) findViewById(R.id.cardInfoTv);
        addBankLayout2 = (RelativeLayout) findViewById(R.id.addBankLayout2);
        addBankLayout2.setOnClickListener(this);
        mPresenter = new DealerWalletPresenter();
        mPresenter.attachView(this);
        mApplyPresenter = new DealerWalletPresenter();
        mApplyPresenter.attachView(mApplyView);

        mBalancePresenter = new DealerWalletPresenter();
        mBalancePresenter.attachView(mBalanceView);

//设置Input的类型两种都要

        cashET.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);

//设置字符过滤
        cashET.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source.equals(".") && dest.toString().length() == 0) {
                    return "0.";
                }
                if (dest.toString().contains(".")) {
                    int index = dest.toString().indexOf(".");
                    int length = dest.toString().substring(index).length();
                    if (length == 3) {
                        return "";
                    }
                }
                if (dest.toString().length() > 16) {

                    return "";
                }
                return null;
            }
        }});

        cashET.addTextChangedListener(mWatcher);
        mBalancePresenter.getAccountBalances(IOUtils.getToken(mContext));
    }

    private AccountBalanceModel mAccountBalanceModel;
    private DealerWalletView<AccountBalanceModel> mBalanceView = new DealerWalletView<AccountBalanceModel>() {
        @Override
        public void showData(AccountBalanceModel accountBalanceModel) {

            mAccountBalanceModel = accountBalanceModel;
        }

        @Override
        public void setLoadingIndicator(boolean active) {

        }

        @Override
        public void showLoadingTasksError() {

        }

        @Override
        public Context getContext() {
            return mContext;
        }
    };

    private TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            String value = s.toString().trim();
            if (TextUtil.isEmpty(value) || (mModel != null && mModel.bankInfo == null)) {
                confirmBtn.setEnabled(false);
            } else {
                confirmBtn.setEnabled(true);
            }
            if (mModel != null && !TextUtil.isEmpty(value)) {
                balanceTv.setText("可用余额：" + mModel.money + "元");
                if (ArithmeticUtil.sub(value, mModel.money).doubleValue() > 0) {
                    confirmBtn.setEnabled(false);
                    balanceTv.setText("金额已超过可提现余额!");
                }
            }
        }
    };

    private DealerWalletView<ResponseMessage<Object>> mApplyView = new DealerWalletView<ResponseMessage<Object>>() {
        @Override
        public void showData(ResponseMessage<Object> resp) {

            if (resp.statusCode == Constants.NetworkStatusCode.SUCCESS) {
                CommonUtils.showToast(mContext, "提现成功!");
                finish();
                //mPresenter.getSelectedBankCard(IOUtils.getToken(mContext));
            } else {

                CommonUtils.showToast(mContext, resp.statusMessage);
            }


        }

        @Override
        public void setLoadingIndicator(boolean active) {

            showLoadingIndicator(active);
        }

        @Override
        public void showLoadingTasksError() {

            dismissLoadingDialog();
        }

        @Override
        public Context getContext() {
            return mContext;
        }
    };

    @Override
    public void onClick(View view) {


        int id = view.getId();

        switch (id) {

            case R.id.confirmBtn:


                applyWithdraw();
                break;

            case R.id.addBankLayout1:

                CommonUtils.startResultNewActivity(this, null, AddBankCardStepFirstActivity.class, Constants.InteractionCode.REQUEST_CODE);

                break;

            case R.id.addBankLayout2:

                CommonUtils.startResultNewActivity(this, null, SelectBankCardActivity.class, Constants.InteractionCode.REQUEST_CODE);

                break;

        }


    }

    private void applyWithdraw() {


        if (mModel == null) {

            CommonUtils.showToast(mContext, "请选择要提现的银行卡");
            return;
        }

        String money = mModel.money;

        String cash = cashET.getText().toString();

        if (Double.parseDouble(cash) > Double.parseDouble(money)) {

            CommonUtils.showToast(mContext, "提现金额不能大于可用余额!");
            return;
        }
        if (Double.parseDouble(cash) <= 0.0d) {

            CommonUtils.showToast(mContext, "提现金额必须大于0!");
            return;
        }

        if (mAccountBalanceModel == null) {

            CommonUtils.showToast(mContext, "正在加载数据...");
            return;
        }
        if (mAccountBalanceModel.isFreePay == 1 && Double.parseDouble(cash) < Double.parseDouble(mAccountBalanceModel.freePayQuota)) {


            mApplyPresenter.applyWithdraw(applyReqParams(""));
            return;
        }
        validatePwd();
    }


    private void validatePwd() {

        final DealerPayPwdValidatePopupWindow payPwdValidatePopupWindow = new DealerPayPwdValidatePopupWindow(this, null);
        payPwdValidatePopupWindow.setValidateListener(new DealerPayPwdValidatePopupWindow.PayPwdValidateListener() {

            @Override
            public void onSuccess(String pwd) {

                payPwdValidatePopupWindow.dismiss();
                mApplyPresenter.applyWithdraw(applyReqParams(pwd));
            }

            @Override
            public void onFailure(String pwd) {

                CommonUtils.showToast(mContext, "输入密码不对，请重新输入！");

            }
        });
        payPwdValidatePopupWindow.show(this);
    }

    private Map<String, String> applyReqParams(String pwd) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("token", IOUtils.getToken(mContext));
        params.put("bank_id", mModel.id + "");
        params.put("money", cashET.getText().toString());
        if (TextUtils.isEmpty(pwd)) {
            params.put("pay_password", pwd);
        } else {
            params.put("pay_password", EncryptUtil.encode(pwd));
        }

        return params;
    }


    @Override
    public void showData(BankCardModel model) {


        showSelectedBankCardInfo(model);
        balanceTv.setText("可用余额：" + model.money + "元");
        mModel = model;
    }

    private void showSelectedBankCardInfo(BankCardModel model) {

        if (model.bankInfo == null) {
            addBankLayout2.setVisibility(View.GONE);
            addBankLayout1.setVisibility(View.VISIBLE);
            confirmBtn.setEnabled(false);
        } else {
            bankName2.setText(model.bankInfo.name);
            cardInfoTv.setText(model.bankInfo.type);
            addBankLayout2.setVisibility(View.VISIBLE);
            addBankLayout1.setVisibility(View.GONE);
        }
    }

    @Override
    public void setLoadingIndicator(boolean active) {

    }

    @Override
    public void showLoadingTasksError() {

    }

    @Override
    public Context getContext() {
        return mContext;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPresenter != null) {
            mPresenter.detachView();
        }

        if (mApplyPresenter != null) {

            mApplyPresenter.detachView();
        }
    }
}

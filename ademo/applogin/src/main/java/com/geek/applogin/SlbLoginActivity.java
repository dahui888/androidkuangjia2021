package com.geek.applogin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.geek.biz1.bean.HLoginBean;
import com.geek.biz1.bean.HUserInfoBean;
import com.geek.biz1.presenter.HErweimaPresenter;
import com.geek.biz1.presenter.HUserInfoPresenter;
import com.geek.biz1.presenter.HYonghudengluPresenter;
import com.geek.biz1.view.HErweimaView;
import com.geek.biz1.view.HUserInfoView;
import com.geek.biz1.view.HYonghudengluView;
import com.geek.libbase.base.SlbBaseActivity;
import com.geek.libbase.utils.YanzhengUtil;
import com.geek.libutils.SlbLoginUtil;
import com.geek.libutils.app.MyLogUtil;
import com.google.android.material.textfield.TextInputLayout;
import com.haier.cellarette.baselibrary.loading.ShowLoadingUtil;
import com.haier.cellarette.baselibrary.qcode.ExpandViewRect;
import com.haier.cellarette.baselibrary.zothers.SpannableStringUtils;
import com.haier.cellarette.libwebview.hois2.HiosHelper;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class SlbLoginActivity extends SlbBaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks, HUserInfoView, HYonghudengluView, HErweimaView {

    private TextView tv_tips12;
    private ImageView iv1;
    private ImageView iv_bg1;
    private ImageView iv_xx;
    private ImageView iv_wx;
    private TextInputLayout textinputlayout1;
    private TextInputLayout textinputlayout2;
    private AppCompatEditText edt1;
    private AppCompatEditText edt2;
    private Button tv_hqyzm;
    private Button tv1;

    private HYonghudengluPresenter presenter;
    private HErweimaPresenter presentercode;
    private HUserInfoPresenter userInfoPresenter;
    //
    private String openid;
    private String unionid;
    private String gender;
    private String nickName;
    private String avatar;
    private String other_login_name = "Wechat";
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_slblogin;
    }

    @Override
    protected void setup(@Nullable Bundle savedInstanceState) {
        super.setup(savedInstanceState);
        findview();
        onclick();
        donetwork();
        //
//        MobclickAgent.onEvent(this, "Loginpage");
    }

    private void donetwork() {
        //
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            tv_tips12.setText(Html.fromHtml("登录即代表您同意" + "<font color=\"#4EC0FF\">用户协议</font>" + "和" + "<font color=\"#4EC0FF\">隐私政策</font>", Html.FROM_HTML_MODE_COMPACT));
//        } else {
//            tv_tips12.setText(Html.fromHtml("登录即代表您同意" + "<font color=\"#4EC0FF\">用户协议</font>" + "和" + "<font color=\"#4EC0FF\">隐私政策</font>"));
//        }
        tv_tips12.setText(SpannableStringUtils.getInstance(this.getApplicationContext())
                .getBuilder("首次登录将自动注册,且代表您已同意")
                .append("《用户协议》")
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
//                        HiosHelper.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, MmkvUtils.getInstance().get_common(CommonUtils.MMKV_serviceProtocol));
                        HiosHelper.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, "https://syzs.qq.com/campaign/3977.html");
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(Utils.getApp().getResources().getColor(R.color.blue519AF4));
                        ds.setUnderlineText(false);
                    }
                })
                .append("和")
                .append("《隐私政策》")
                .setClickSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
//                Uri url = Uri.parse("http://blog.51cto.com/liangxiao");
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(url);
//                startActivity(intent);
//                        HiosHelper.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, MmkvUtils.getInstance().get_common(CommonUtils.MMKV_privacyPolicy));
                        HiosHelper.resolveAd(SlbLoginActivity.this, SlbLoginActivity.this, "https://syzs.qq.com/campaign/3977.html");
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(Utils.getApp().getResources().getColor(R.color.blue519AF4));
                        ds.setUnderlineText(false);
                    }
                })
                .create());
        tv_tips12.setMovementMethod(LinkMovementMethod.getInstance());

        //
//        if (TextUtils.equals("1", SPUtils.getInstance().getString(CommonUtils.MMKV_forceLogin))) {
//            iv1.setVisibility(View.INVISIBLE);
//        } else {
//            iv1.setVisibility(View.VISIBLE);
//        }
        presenter = new HYonghudengluPresenter();
        presenter.onCreate(this);
        presentercode = new HErweimaPresenter();
        presentercode.onCreate(this);

        // 获取资料bufen
        userInfoPresenter = new HUserInfoPresenter();
        userInfoPresenter.onCreate(this);
//        jPushDengluUtils.shouquan_cancel(other_login_name);

    }

    private void onclick() {
        iv_xx.setOnClickListener(this);
        iv1.setOnClickListener(this);
        iv_wx.setOnClickListener(this);
        tv_hqyzm.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                // 获取验证码bufen  1
                hqyzm();
            }
        });
        tv1.setOnClickListener(this);
//        jPushDengluUtils = new JPushDengluUtils(new OnResultInfoLitener() {
//            @Override
//            public void onResults(String platform, String toastMsg, String data) {
////                Toasty.normal(BaseApp.get(), platform + "---" + toastMsg + "---" + data).show();
//                // 微信登录成功bufen
//                JSONObject jsonObject = JSONObject.parseObject(data);
//                if (jsonObject == null) {
//                    return;
//                }
//                openid = (String) jsonObject.get("openid");
//                nickName = (String) jsonObject.get("nickname");
//                gender = (int) jsonObject.get("sex") + "";
//                avatar = (String) jsonObject.get("headimgurl");
//                unionid = (String) jsonObject.get("unionid");
////                presenter.getWeChatLoginData(openid, unionid, gender, nickName, avatar);
//            }
//        });
        edt1.addTextChangedListener(textWatcher);
        edt2.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String a = edt1.getText().toString().trim();
            String b = edt2.getText().toString().trim();
            if (TextUtils.isEmpty(a)) {
                iv_xx.setVisibility(View.GONE);
            } else {
                iv_xx.setVisibility(View.VISIBLE);
            }
            if (!TextUtils.isEmpty(a) && !TextUtils.isEmpty(b) && charSequence.length() > 0) {
                tv1.setEnabled(true);
                tv1.setBackgroundResource(R.drawable.btn_denglu2);
            } else {
                tv1.setEnabled(false);
                tv1.setBackgroundResource(R.drawable.btn_denglu2_enpressed);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void findview() {
        tv_tips12 = findViewById(R.id.tv_tips12);
        iv1 = findViewById(R.id.iv1);
        iv_bg1 = findViewById(R.id.iv_bg1);
        iv_xx = findViewById(R.id.iv_xx);
        ExpandViewRect.expandViewTouchDelegate(iv_xx, 30, 30, 30, 30);
        iv_wx = findViewById(R.id.iv_wx);
        textinputlayout1 = findViewById(R.id.textinputlayout1);
        textinputlayout2 = findViewById(R.id.textinputlayout2);
        edt1 = findViewById(R.id.edt1);
        edt2 = findViewById(R.id.edt2);
        tv_hqyzm = findViewById(R.id.tv_hqyzm);
        tv1 = findViewById(R.id.tv1);
        tv1.setEnabled(false);
        tv1.setBackgroundResource(R.drawable.btn_denglu2_enpressed);

    }


    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        MyLogUtil.d("SMS1", "onPermissionsGranted:" + requestCode + ":" + perms.size());
        // 获取验证码bufen
        hqyzm();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        MyLogUtil.d("SMS1", "onPermissionsDenied:" + requestCode + ":" + perms.size());
//        Toasty.normal(SlbLoginActivity.this, "您阻止了app读取您的短信，您可以自己手动输入验证码").show();
        edt2.requestFocus();
    }

    @Override
    protected void onActResult(int requestCode, int resultCode, Intent data) {
        super.onActResult(requestCode, resultCode, data);
        if (requestCode == 10016) {
            // 获取验证码bufen
            hqyzm();
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv1) {
            // 登录bufen
            //
//            MobclickAgent.onEvent(this, "Loginpage_button");
            if (!is_login()) {
                return;
            }
            String b = edt2.getText().toString().trim();
            if (TextUtils.isEmpty(b)) {
                ToastUtils.showLong("请输入验证码");
//                YanzhengUtil.showError(textinputlayout2, "请输入验证码");
                return;
            }
            denglu();
        } else if (i == R.id.iv1) {
            // 关闭bufen
            onBackPressed();
        } else if (i == R.id.iv_wx) {
            // 微信登录
            wxdl();
        } else if (i == R.id.iv_xx) {
            // XX
            edt1.setText("");
        } else {

        }
    }

    private void onLoginSuccess() {
        setResult(SlbLoginUtil.LOGIN_RESULT_OK);
        if (ActivityUtils.getActivityList().size() == 1) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.ShouyeActivity"));
        }
        finish();
    }

    private void onLoginCanceled() {
        setResult(SlbLoginUtil.LOGIN_RESULT_CANCELED);
        if (ActivityUtils.getActivityList().size() == 1) {
            startActivity(new Intent(AppUtils.getAppPackageName() + ".hs.act.slbapp.ShouyeActivity"));
        }
        finish();
    }

    /**
     * 不登录操作
     */
    private void donetloginno() {
        //step 请求服务器成功后清除sp中的数据
//        SpUtils.get(this).get("", "");
        onLoginCanceled();
    }

    private boolean is_login() {
        String a = edt1.getText().toString().trim();
        if (TextUtils.isEmpty(a)) {
            ToastUtils.showLong("请输入您的手机号");
//            YanzhengUtil.showError(textinputlayout1, "请输入您的手机号");
            return false;
        }
        if (!RegexUtils.isMobileSimple(a)) {
            ToastUtils.showLong("手机号格式不正确");
//            YanzhengUtil.showError(textinputlayout1, "请输入您的手机号");
            return false;
        }
        return true;
    }

    /**
     * 获取验证码
     */
    private void hqyzm() {
        if (!is_login()) {
            return;
        }
        YanzhengUtil.startTime(60 * 1000, tv_hqyzm);
        //接口通了后执行下一步
        presentercode.get_erweima(edt1.getText().toString().trim());

    }

    @Override
    protected void onDestroy() {
        presenter.onDestory();
        presentercode.onDestory();
        userInfoPresenter.onDestory();
        YanzhengUtil.timer_des();
        if (mHandler != null) {
            mHandlerThread.quit();
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
//        MobclickAgent.onEvent(this, "Loginpage_closebutton");
        no_denglu();
        super.onBackPressed();
    }

    /**
     *
     *
     * ----------------------------------下面为业务逻辑--分割线------------------------------
     *
     */


    /**
     * 登录
     */
    private void denglu() {
//        Toasty.normal(this, "登录").show();
        presenter.get_yonghudenglu("android", edt1.getText().toString().trim(), edt2.getText().toString().trim());

    }

    /**
     * 不登录 返回原来的
     */
    private void no_denglu() {
        donetloginno();
    }

    // 微信登录
    private void wxdl() {
//        jPushDengluUtils.shouquan(other_login_name);
        ShowLoadingUtil.showLoading(SlbLoginActivity.this, "", null);
        // test 可注掉
        SPUtils.getInstance().put("token", "token");
        onLoginSuccess();
    }

    // 获取验证码bufen
    @Override
    public void OnErweimaSuccess(String s) {
        edt2.requestFocus();
    }

    @Override
    public void OnErweimaNodata(String s) {
        ToastUtils.showLong(s);
    }

    @Override
    public void OnErweimaFail(String s) {
        ToastUtils.showLong(s);
    }

    @Override
    public void OnYonghudengluSuccess(HLoginBean hLoginBean) {
//        MmkvUtils.getInstance().set_common(CommonUtils.MMKV_forceLogin, "0");
//        MmkvUtils.getInstance().set_common(CommonUtils.MMKV_TOKEN, hLoginBean.getToken());
        userInfoPresenter.get_userinfo();
//        onLoginSuccess();
    }

    @Override
    public void OnYonghudengluNodata(String s) {
        ToastUtils.showLong(s);
//        set_token_out();
    }

    @Override
    public void OnYonghudengluFail(String s) {
        ToastUtils.showLong(s);
        //TODO test
        userInfoPresenter.get_userinfo();
//        set_token_out();
    }

    private void set_token_out() {
        ToastUtils.showLong("登录失效，请重新登录！");
//        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_SEX);
//        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_IMG);
//        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_TEL);
//        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_NAME);
//        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_forceLogin);
//        MmkvUtils.getInstance().remove_common(CommonUtils.MMKV_TOKEN);
    }

    @Override
    public void OnUserInfoSuccess(HUserInfoBean bean) {
//        MmkvUtils.getInstance().set_common(CommonUtils.MMKV_SEX, bean.getChildGender());
//        MmkvUtils.getInstance().set_common(CommonUtils.MMKV_IMG, bean.getChildAvatar());
//        MmkvUtils.getInstance().set_common(CommonUtils.MMKV_NAME, bean.getNikeName());
//        MmkvUtils.getInstance().set_common(CommonUtils.MMKV_TEL, bean.getPhone());
        onLoginSuccess();
    }

    @Override
    public void OnUserInfoNodata(String s) {
        onLoginSuccess();
    }

    @Override
    public void OnUserInfoFail(String s) {
        onLoginSuccess();
    }
}

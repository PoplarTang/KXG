package com.cqts.kxg.center;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.base.BaseValue;
import com.base.utils.MyGridDecoration;
import com.base.views.MyEditText;
import com.base.zxing.EncodingHandler;
import com.cqts.kxg.R;
import com.cqts.kxg.adapter.ApprenticeAdapter;
import com.cqts.kxg.bean.EaringApprenticeInfo;
import com.cqts.kxg.bean.MyApprenticeInfo;
import com.cqts.kxg.main.MyActivity;
import com.cqts.kxg.utils.MyHttp;
import com.cqts.kxg.utils.ShareUtilsWB;
import com.cqts.kxg.views.SharePop;
import com.google.zxing.WriterException;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.constant.WBConstants;

import java.util.ArrayList;

/**
 * 收徒弟页面
 */
public class ApprenticeActivity extends MyActivity implements View.OnClickListener{
    private TextView table1Tv, table2Tv, table3Tv, table4Tv;
    private MyEditText invitationEt;
    private Button shareBtn;
    private ImageView empty1Img, empty2Img, qrImg;
    private TextView change_tv;
    MyApprenticeInfo myApprenticeInfo;
    ArrayList<MyApprenticeInfo.Apprentice> signup = new ArrayList<>();
    ArrayList<MyApprenticeInfo.Apprentice> task = new ArrayList<>();
    private ApprenticeAdapter adapter1, adapter2;
    private RecyclerView recyclerview1, recyclerview2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apprentice);
        InitView();
        InitRC1();
        InitRC2();
        getData();
    }

    private void InitRC1() {
        GridLayoutManager manager = new GridLayoutManager(this, 6);
        MyGridDecoration gridDecoration = new MyGridDecoration(0, 0, Color.WHITE, true);
        adapter1 = new ApprenticeAdapter(task);
        recyclerview1.setLayoutManager(manager);
        recyclerview1.addItemDecoration(gridDecoration);
        recyclerview1.setAdapter(adapter1);
    }

    private void InitRC2() {
        GridLayoutManager manager = new GridLayoutManager(this, 6);
        MyGridDecoration gridDecoration = new MyGridDecoration(0, 0, Color.WHITE, true);
        adapter2 = new ApprenticeAdapter(signup);
        recyclerview2.setLayoutManager(manager);
        recyclerview2.addItemDecoration(gridDecoration);
        recyclerview2.setAdapter(adapter2);
    }

    private void InitView() {
        setMyTitle("收徒");
        change_tv = (TextView) findViewById(R.id.change_tv);
        table1Tv = (TextView) findViewById(R.id.table1_tv);
        table2Tv = (TextView) findViewById(R.id.table2_tv);
        table3Tv = (TextView) findViewById(R.id.table3_tv);
        table4Tv = (TextView) findViewById(R.id.table4_tv);

        qrImg = (ImageView) findViewById(R.id.qr_img);
        invitationEt = (MyEditText) findViewById(R.id.invitation_et);
        shareBtn = (Button) findViewById(R.id.share_btn);
        empty1Img = (ImageView) findViewById(R.id.empty1_img);
        recyclerview1 = (RecyclerView) findViewById(R.id.recyclerview1);
        recyclerview2 = (RecyclerView) findViewById(R.id.recyclerview2);
        empty2Img = (ImageView) findViewById(R.id.empty2_img);
        change_tv.setOnClickListener(this);
        shareBtn.setOnClickListener(this);
        invitationEt.setText(getUserInfo().invite_code + "");

        try {
            //生成二维码
            Bitmap qrcodeBitmap = EncodingHandler.createQRCode(getUserInfo().invite_link +
                    getUserInfo().invite_code, 400);
            qrImg.setImageBitmap(qrcodeBitmap);
            qrImg.setScaleType(ImageView.ScaleType.FIT_XY);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void getData() {
        //查询徒弟收益信息
        MyHttp.userApprentice(http, null, new MyHttp.MyHttpResult() {
            @Override
            public void httpResult(Integer which, int code, String msg, Object bean) {
                if (code != 0) {
                    showToast(msg);
                    return;
                }

                EaringApprenticeInfo apprenticeInfo = (EaringApprenticeInfo) bean;
                table1Tv.setText(TextUtils.isEmpty(apprenticeInfo.total) ? "0" : apprenticeInfo
                        .total + "人");
                table2Tv.setText(TextUtils.isEmpty(apprenticeInfo.today) ? "0" : apprenticeInfo
                        .today + "人");
                table3Tv.setText(String.format("%.2f", apprenticeInfo.apprentice) + "元");
                table4Tv.setText(TextUtils.isEmpty(apprenticeInfo.shared) ? "0" : apprenticeInfo
                        .shared + "次");
            }
        });

        //查询答题和为答题的徒弟
        MyHttp.apprenticeListing(http, null, new MyHttp.MyHttpResult() {
            @Override
            public void httpResult(Integer which, int code, String msg, Object bean) {
                if (code != 0) {
                    showToast(msg);
                    empty1Img.setVisibility(View.VISIBLE);
                    empty2Img.setVisibility(View.VISIBLE);
                    recyclerview2.setVisibility(View.GONE);
                    recyclerview1.setVisibility(View.GONE);
                    return;
                }
                myApprenticeInfo = (MyApprenticeInfo) bean;
                if (myApprenticeInfo != null && myApprenticeInfo.signup != null &&
                        myApprenticeInfo.signup.size() != 0) {
                    signup.addAll(myApprenticeInfo.signup);
                    adapter1.notifyDataSetChanged();
                    recyclerview1.setVisibility(View.VISIBLE);
                    empty1Img.setVisibility(View.GONE);
                } else {
                    empty1Img.setVisibility(View.VISIBLE);
                    recyclerview1.setVisibility(View.GONE);
                }

                if (myApprenticeInfo != null && myApprenticeInfo.task != null && myApprenticeInfo
                        .task.size() != 0) {
                    task.addAll(myApprenticeInfo.task);
                    adapter2.notifyDataSetChanged();
                    recyclerview2.setVisibility(View.VISIBLE);
                    empty2Img.setVisibility(View.GONE);
                } else {
                    empty2Img.setVisibility(View.VISIBLE);
                    recyclerview2.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.change_tv: //改变邀请码
                changeCode();
                break;
            case R.id.share_btn: //分享给徒弟
                String title = (TextUtils.isEmpty(getUserInfo().alias) ? "" : ("\"" + getUserInfo
                        ().alias + "\"")) + "推荐给你“开心购久久app”，注册后有红包哦！";
                String url = getUserInfo().invite_link + getUserInfo().invite_code;
                String text = "您可以在这里浏览购买数百万商品，更有9.9包邮等特价专区！";
                SharePop.getInstance().showPop(this, shareBtn, title, url, text, null, null, null);
                break;
            default:
                break;
        }
    }

    //修改邀请码
    private void changeCode() {
        final String text = invitationEt.getText().toString().trim();
        boolean enabled = invitationEt.isEnabled();

        if (!enabled) {
            BaseValue.imm.showSoftInput(invitationEt, 0);//展开输入法
            change_tv.setText("确认");
            invitationEt.setEnabled(true);
            if (!TextUtils.isEmpty(text)) {
                invitationEt.setSelection(text.length());
            }
        }

        if (enabled) {
            if (text.isEmpty() || text.length() < 6) {
                showToast("请输入6位邀请码");
                return;
            }
            MyHttp.userInvitecode(http, null, text, new MyHttp.MyHttpResult() {
                @Override
                public void httpResult(Integer which, int code, String msg, Object bean) {
                    showToast(msg);
                    if (code == 0) {
                        BaseValue.imm.hideSoftInputFromWindow(invitationEt.getWindowToken(), 0); //关闭输入法
                        invitationEt.setText(text);
                        getUserInfo().invite_code = text;
                        change_tv.setText("修改");
                        invitationEt.setEnabled(false);
                    }
                }
            });
        }
    }
}

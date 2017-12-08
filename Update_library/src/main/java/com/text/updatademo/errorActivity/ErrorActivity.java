package com.text.updatademo.errorActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.text.updatademo.R;
import com.text.updatademo.databean.ErrorDataBean;
import com.text.updatademo.net.OkHttpCallback;
import com.text.updatademo.net.OkHttpClient;
import com.text.updatademo.utils.CompleteQuit;
import com.text.updatademo.utils.Constants;
import com.text.updatademo.utils.GetVerInfo;
import com.text.updatademo.utils.SignUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 程序崩溃用户提示界面
 *
 * @author ht
 */
public class ErrorActivity extends Activity {
    private static final String TAG = "ErrorActivity";

    /**
     * 消息
     */
    private String mMessage;
    private String version;
    private String phone_version;
    /**
     * 表头提示
     */
    private TextView tvTitle;
    /**
     * 提示语
     */
    private String mIdea;
    /**
     * 选择按钮
     */
    private CheckBox mRreportCB;
    /**
     * 描述输入框
     */
    private EditText mUserIdeaET;
    /**
     * 提交进度条
     */
    private ProgressDialog progressDialog;
    /**
     * 消息提示语
     */
    private Message mMsg;
    private Map<String, String> map;
    /**
     * 签名
     */
    private String sign;

    private String APPID;
    private String Url;
    private String projectName;

    private String errorUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        this.setContentView(R.layout.layout_report_error);
        mUserIdeaET = (EditText) this.findViewById(R.id.et_user_idea);
        mRreportCB = (CheckBox) this.findViewById(R.id.ckb_report);
        tvTitle = (TextView) this.findViewById(R.id.error_title);
        //默认已经选中
        mRreportCB.setChecked(true);
        mMessage = getIntent().getStringExtra("msg");
        APPID = getIntent().getStringExtra("APPID");
        Url = getIntent().getStringExtra("Url");
        projectName = getIntent().getStringExtra("projectName");
        tvTitle.setText(projectName + getResources().getString(R.string.error_tips));
        errorUrl = Url + Constants.ERROR_SYSTEM_URL;
        //手机信息
        phone_version = "Device：" + Build.MANUFACTURER + "-" + Build.MODEL + ","
                + "SDK：" + Build.VERSION.SDK + ","
                + "Android Version：" + Build.VERSION.RELEASE;
        try {
            version = GetVerInfo.getVersionName(ErrorActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            version = "0.0.0";
        }

    }

    public void forClick(View v) {
        if (v.getId() == R.id.btn_report) {
            //重启应用
//            report(1);
            restart();
        } else if (v.getId() == R.id.btn_close) {
            //关闭应用
//            report(-1);
            quit();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //用户点击back按钮后也要上传错误到服务器中
        this.report(-1);
    }

    /**
     * 发送错误报告
     */
    private void report(final int code) {
        mMsg = new Message();
        mMsg.what = code;
        mIdea = this.mUserIdeaET.getText().toString();
        map = new HashMap<>();
        map.put("err.app_id", APPID);
        map.put("err.version", version);
        map.put("err.phone_version", phone_version);
        if (mRreportCB.isChecked()) {
            if (mUserIdeaET.getText() == null
                    || mUserIdeaET.getText().toString().equals("")) {
                mUserIdeaET.requestFocus();
                mUserIdeaET.setError("请您输入问题描述!");
            } else {
                if (isNetConected()) {
                    //网络连接
                    progressDialog =
                            ProgressDialog.show(ErrorActivity.this, "",
                                    "正在提交,请稍候...", true);
                    progressDialog.show();
                    mMessage = "user agree：" + mIdea + "\n" + mMessage;
                    map.put("err.error_text", mMessage);
                    sign = SignUtils.sign(APPID, map);
                    OkHttpClient.getInstance()
                            .saveErrorMsg(errorUrl, sign, APPID, version, phone_version, mMessage,
                                    new OkHttpCallback<ErrorDataBean>() {
                                        @Override
                                        public void onSuccess(ErrorDataBean response, int id) {
                                            mMsg.obj = true;
                                            handler.sendMessage(mMsg);
                                        }

                                        @Override
                                        public void onError(Exception e, int id) {
                                            mMsg.obj = true;
                                            handler.sendMessage(mMsg);
                                        }
                                    });

                } else {
                    Toast.makeText(ErrorActivity.this,
                            "网络连接错误，请检查您的手机是否联网!", Toast.LENGTH_SHORT).show();
                    handler.sendMessage(mMsg);
                }
            }
        } else {
            //没有选择提交报告
            if (isNetConected()) {
                progressDialog =
                        ProgressDialog.show(ErrorActivity.this, "",
                                "正在提交,请稍候...", true);
                map.put("err.error_text", mMessage);
                sign = SignUtils.sign(APPID, map);
                OkHttpClient.getInstance()
                        .saveErrorMsg(errorUrl, sign, APPID, version, phone_version, mMessage,
                                new OkHttpCallback<ErrorDataBean>() {
                                    @Override
                                    public void onSuccess(ErrorDataBean response, int id) {
                                        handler.sendMessage(mMsg);
                                    }

                                    @Override
                                    public void onError(Exception e, int id) {
                                        handler.sendMessage(mMsg);
                                    }
                                });
            } else {
                handler.sendMessage(mMsg);
            }
        }

    }

    /**
     * 重启
     */
    private void restart() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //测试
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        reStartApp(intent);


//        try {
//            //TODO:怎么获取程序的入口Activity
//            Intent mIntent = new Intent();
//            ComponentName comp = new ComponentName(
//                    getPackageName(),
//                    getLocalClassName());
//            mIntent.setComponent(comp);
//            mIntent.setAction("android.intent.action.MAIN");
//            this.startActivity(mIntent);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        this.finish();
    }

    /**
     * 获取程序的入口Activity
     * 根据包名来启动
     *
     * @param intent
     */
    private void reStartApp(Intent intent) {
        //获取程序的入口Activity
        List<ResolveInfo> packageInfos = getPackageManager()
                .queryIntentActivities(intent, 0);
        String launcherActivityName = null;
        String packageName;

        for (int i = 0; i < packageInfos.size(); i++) {
            packageName = packageInfos.get(i).activityInfo.packageName;
            if (packageName.equals(getPackageName())) {
                launcherActivityName = packageInfos.get(i).activityInfo.name;
                try {
                    //TODO:怎么获取程序的入口Activity
                    Intent mIntent = new Intent();
                    ComponentName comp = new ComponentName(
                            packageName,
                            launcherActivityName);
                    mIntent.setComponent(comp);
                    mIntent.setAction("android.intent.action.MAIN");
                    this.startActivity(mIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    /**
     * 退出
     */
    protected void quit() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        am.restartPackage(getPackageName());
        finish();
        CompleteQuit.getInstance().exitAll(true);
    }

    /**
     * 判断手机是否联网
     *
     * @return
     */
    private boolean isNetConected() {
        try {
            ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cManager.getActiveNetworkInfo();
            if (info != null && info.isAvailable()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (progressDialog != null) {
                progressDialog.dismiss();
            }
            switch (msg.what) {
                case 1:
                    restart();
                    break;
                case -1:
                    quit();
                    break;
            }
            if (msg.obj != null && (Boolean) msg.obj) {
                Toast.makeText(ErrorActivity.this, "错误报告提交成功!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    };

}

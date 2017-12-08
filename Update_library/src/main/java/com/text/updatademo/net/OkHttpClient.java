package com.text.updatademo.net;


import android.util.Log;

import com.google.gson.Gson;
import com.text.updatademo.databean.ErrorDataBean;
import com.text.updatademo.databean.UpdateDataBean;
import com.text.updatademo.utils.Constants;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

import static android.content.ContentValues.TAG;


/**
 * Created by Liu_xg on 2017/3/2.
 */

public class OkHttpClient {
    //单列模式
    private static OkHttpClient mAgriClient;

    private Gson mGson;

    public OkHttpClient() {
        mGson = new Gson();
    }

    public static OkHttpClient getInstance() {
        if (mAgriClient == null) {
            //synchronized锁死
            synchronized (OkHttpClient.class) {
                if (mAgriClient == null) {
                    mAgriClient = new OkHttpClient();
                }
            }
        }
        return mAgriClient;
    }


    /**
     * 获取更新信息
     *
     * @param callback
     */
    public void getUpdateMsg(String url, String appId, String version,
                             final OkHttpCallback<UpdateDataBean> callback) {
        OkHttpUtils.post()
                .url(url)
                .addParams("appId", appId)
                .addParams("version", version)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onError(e, id);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        UpdateDataBean DataBean = mGson
                                .fromJson(response, UpdateDataBean.class);
                        callback.onSuccess(DataBean, id);
                    }
                });

    }

    /**
     * 保存错误信息到result服务器
     *
     * @param callback
     */
    public void saveErrorMsg(String url,
                             String sign,
                             String app_id,
                             String version,
                             String phone_version,
                             String error_text,
                             final OkHttpCallback<ErrorDataBean> callback) {
        OkHttpUtils.post()
                .url(url)
                .addParams("sign", sign)
                .addParams("err.app_id", app_id)
                .addParams("err.version", version)
                .addParams("err.phone_version", phone_version)
                .addParams("err.error_text", error_text)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onError(e, id);
                        Log.i(TAG, "onResponse: " + e);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i(TAG, "onResponse11: " + response);
                        ErrorDataBean DataBean = mGson
                                .fromJson(response, ErrorDataBean.class);
                        callback.onSuccess(DataBean, id);
                    }
                });

    }

}

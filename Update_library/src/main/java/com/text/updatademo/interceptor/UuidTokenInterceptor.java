package com.text.updatademo.interceptor;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.text.updatademo.utils.Constants;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class UuidTokenInterceptor implements Interceptor {
    private String TAG = "UuidTokenInterceptor";
    private Context context;

    public UuidTokenInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();

        //判断是否为登录接口 如果是 就直接返回
        String uri = original.url().toString();
        if (uri.indexOf("login") > -1) {
            return chain.proceed(original);
        }
        //添加uuid公共参数
        char perfix = '?';
        if (uri.indexOf(perfix) > -1) {
            perfix = '&';
        }
        String parms = "uuid=" + Constants.UUID;
        uri += perfix + parms;
        original = original.newBuilder().url(uri).build();

        //对响应进行判断 code==304 代表 被挤下线
        Response response = chain.proceed(original);
        String content = response.body().string();
        MediaType mediaType = response.body().contentType();
        return response.newBuilder()
                .body(ResponseBody.create(mediaType, content))
                .build();
//        return response;
    }
}

package com.text.updatademo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 包名： com.gstb.agriculture.utils
 * 创建人： Liu_xg
 * 时间： 2017/11/1 9:15
 * 描述： 获取app的版本信息帮助类
 * 修改人：
 * 修改时间：
 * 修改备注：
 */
public class GetVerInfo {
    /**
     * 获取VersionName
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context context) throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        String version = packInfo.versionName;
        return version;
    }

    /**
     * 获取VersionName
     *
     * @param context
     * @return
     * @throws Exception
     */
    public static String getVersionCode(Context context) throws Exception {
        // 获取packagemanager的实例
        PackageManager packageManager = context.getPackageManager();
        // getPackageName()是你当前类的包名，0代表是获取版本信息
        PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
        String version = packInfo.versionName;
        int code = packInfo.versionCode;
        return version;
    }
}

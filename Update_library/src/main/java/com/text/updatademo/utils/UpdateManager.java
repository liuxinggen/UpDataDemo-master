package com.text.updatademo.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.text.updatademo.databean.UpdateDataBean;
import com.text.updatademo.listenter.Callback;
import com.text.updatademo.listenter.OnDownAPKListenter;
import com.text.updatademo.net.OkHttpCallback;
import com.text.updatademo.net.OkHttpClient;
import com.text.updatademo.view.ConfirmDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * 类名： UpdateManager
 * 时间：2017/11/20 14:57
 * 描述： 更新帮助类
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 * @author Liu_xg
 */
public class UpdateManager {

    /**
     * 单列模式
     **/
    private static UpdateManager mUpdateManager = null;

    /**
     * 构造函数私有化
     **/
    private UpdateManager() {
    }

    /**
     * 公有的静态函数，对外暴露获取单例对象的接口
     **/
    public static UpdateManager getInstance() {
        if (mUpdateManager == null) {
            synchronized (UpdateManager.class) {
                if (mUpdateManager == null) {
                    mUpdateManager = new UpdateManager();
                }
            }
        }
        return mUpdateManager;
    }


    private static final String TAG = "UpdateManager";

    private Context mContext;

    /**
     * 返回的安装包url
     */
    private String apkDownUrl;
    /**
     * 下载包安装路径
     */
    private static final String savePath = "/storage/emulated/0/agricultural_update_apk/";

    private static final String saveFileName = savePath + "UpdateRelease.apk";

    private static final int DOWN_UPDATE1 = 0;

    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private static final int DOWN_NONTIFICATION = 3;

    private int progress;

    private Thread downLoadThread;

    private OnDownAPKListenter onDownAPKListenter;

    private boolean interceptFlag = false;

    private String content = "";

    private ConfirmDialog confirmDialog;

    private String versionName;

    private int code;
    private String msg;
    private UpdateDataBean.DataBean data;
    private long create_time;
    private String file_id;
    private String ids;
    private int fileSize;
    private String version;
    private List<UpdateDataBean.DataBean.DescsBean> descs;

    private String projectName;
    private String HttpUrl;

    /**
     * @param context     上下文
     * @param APPID       该产品对应的APPID
     * @param HttpUrl     该产品的url地址，列如：http://211.149.244.64:8099/
     * @param projectName 该产品的名称，列如：智慧农业
     * @return
     */
    public UpdateManager initUpdateMsg(Context context, String APPID,
                                       String HttpUrl, String projectName) {

        this.mContext = context;
        this.projectName = projectName;
        this.HttpUrl = HttpUrl;
        /**
         * 判断版本是否要更新
         */
        try {
            versionName = GetVerInfo.getVersionName(context);
        } catch (Exception e) {
            e.printStackTrace();
            versionName = "0";
        }
        String url = HttpUrl + Constants.UPDATE_SYSTEM_URL;
        OkHttpClient.getInstance().getUpdateMsg(url, APPID, versionName,
                new OkHttpCallback<UpdateDataBean>() {
                    @Override
                    public void onSuccess(UpdateDataBean response, int id) {
                        code = response.getCode();
                        msg = response.getMsg();
                        data = response.getData();
                        if (code == Constants.UPDATE_SUCCESS) {
                            //获取更新成功
                            create_time = data.getCreate_time();
                            fileSize = data.getFileSize();
                            file_id = data.getFile_id();
                            ids = data.getId();
                            version = data.getVersion();
                            descs = data.getDescs();
                            //更新
                            mUpdateManager.updateShowDialog(create_time, file_id, fileSize,
                                    ids, version, descs);
                        } else if (code == Constants.UPDATE_ERROR) {
                            //获取更新失败，未找到当前应用
                            getOnDownAPKListenter().onCancel();
                        } else if (code == Constants.UPDATE_ALREADY_NEW) {
                            //获取更新失败，当前版本为最新版本
                            getOnDownAPKListenter().onCancel();
                        }
                    }

                    @Override
                    public void onError(Exception e, int id) {
                        //TODO:网络连接错误
                        Log.i(TAG, "onError: ");
                        getOnDownAPKListenter().onCancel();
                    }
                });

        return this;
    }


    /**
     * 更新
     */
    private void updateShowDialog(long create_time, String file_id, int fileSize, String ids,
                                  String version, List<UpdateDataBean.DataBean.DescsBean> listDescs) {
        apkDownUrl = HttpUrl + Constants.DOWN_SYSTEM_URL + file_id;
        for (int i = 0; i < listDescs.size(); i++) {
            content += listDescs.get(i).getDesc() + "\n";
        }
        double size = (fileSize / 1024) / 1024;
        confirmDialog = new ConfirmDialog(mContext, new Callback() {
            @Override
            public void callback(int position) {
                if (position == 1) {
                    confirmDialog.isEnabled();
                    if (isWifiConnected(mContext)) {
                        //wifi状态
                        downloadApk();
                    } else {
                        new ConfirmDialog(mContext, new Callback() {
                            @Override
                            public void callback(int position) {
                                if (position == 1) {
                                    downloadApk();
                                } else {
                                    confirmDialog.dimiss();
                                    interceptFlag = true;
                                    if (getOnDownAPKListenter() != null) {
                                        getOnDownAPKListenter().onCancel();
                                    }
                                }
                            }
                        }).setContent("目前手机不是WiFi状态\n确认是否继续下载更新？").show();
                    }
                } else {
                    confirmDialog.dimiss();
                    interceptFlag = true;
                    if (getOnDownAPKListenter() != null) {
                        getOnDownAPKListenter().onCancel();
                    }
                }
            }
        });

        confirmDialog.setContent(projectName + "v" + version + "新特性：\n" +
                content +
                "\n安装包大小：" + String.format("%.2f", size) + "MB");
        confirmDialog.show();
    }

    private Runnable mdownApkRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(apkDownUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();
                File file = new File(savePath);
                //先删除之前的异常信息
                if (file.exists()) {
                    DeleteFile(file);
                }
                if (!file.exists()) {
                    file.mkdir();
                }
                String apkFile = saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);

                int count = 0;
                byte[] buf = new byte[1024];
                while (!interceptFlag) {
                    int numread = is.read(buf);
                    count += numread;
                    progress = (int) (((float) count / length) * 100);
                    //更新进度
                    if (progress >= 96) {
                        mHandler.sendEmptyMessage(DOWN_UPDATE1);
                    } else {
                        mHandler.sendEmptyMessage(DOWN_UPDATE);
                    }
                    if (numread <= 0) {
                        //下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                    //点击取消就停止下载.
                }

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    /**
     * 下载apk
     */

    private void downloadApk() {
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 安装apk
     */
    private void installApk() {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            Toast.makeText(mContext, "下载的安装包不存在", Toast.LENGTH_SHORT).show();
            return;
        }
        //判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //在AndroidManifest中的android:authorities值
            Uri apkUri = FileProvider.getUriForFile(mContext,
                    mContext.getPackageName() + ".provider", apkfile);
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            mContext.startActivity(install);
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(install);
        }
    }


    public OnDownAPKListenter getOnDownAPKListenter() {
        return onDownAPKListenter;
    }

    public void setOnDownAPKListenter(OnDownAPKListenter onDownAPKListenter) {
        this.onDownAPKListenter = onDownAPKListenter;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    confirmDialog.setSureText("正在下载..." + progress + "%");
                    break;
                case DOWN_OVER:
                    Toast.makeText(mContext, "下载成功", Toast.LENGTH_SHORT).show();
                    confirmDialog.dimiss();
                    //TODO: 把应用关闭
                    installApk();
                    break;
                case DOWN_NONTIFICATION:
                    break;
                case DOWN_UPDATE1:
                    confirmDialog.setSureText("正在校验...");
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 检测wifi是否连接
     */
    private static boolean isWifiConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    private void DeleteFile(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                DeleteFile(f);
            }
            file.delete();
        }
    }

}

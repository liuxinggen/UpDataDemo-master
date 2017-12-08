package com.text.updatademo.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import com.text.updatademo.interceptor.UuidTokenInterceptor;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 类名：com.gstb.agriculture.utils
 * 时间：2017/11/13 10:05
 * 描述： 错误日志收集，
 * UncaughtException处理类,
 * 当程序发生Uncaught异常的时候,有该类来接管程序,并记录发送错误报告.
 * 需要在Application中注册，为了要在程序启动器就监控整个程序。
 * 修改人：
 * 修改时间：
 * 修改备注：
 *
 * @author Liu_xg
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {


    private static final String TAG = "CrashHandler";
    private static final boolean DEBUG = true;
    private static final boolean SAVESDCARD = true;
    private static final String PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath()
            + File.separator
            + File.separator;

    private static final String FILE_NAME = "agriculture_crash";
    private static final String FILE_NAME_SUFFIX = ".log";


    private static CrashHandler sInstance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultCrasHandler;

    private Context mContext;

    private String APPID;
    private String Url;
    private String projectName;

    /**
     * 使用Properties来保存设备的信息和错误堆栈信息
     */
    private Properties mDeviceCrashInfo = new Properties();
    private static final String STACK_TRACE = "STACK_TRACE";

    public CrashHandler() {

    }

    public static CrashHandler getsInstance() {
        return sInstance;
    }

    public void init(Context context, String APPID, String Url, String projectName) {
        this.APPID = APPID;
        this.Url = Url;
        this.projectName = projectName;
        mDefaultCrasHandler = Thread.getDefaultUncaughtExceptionHandler();
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
        /**
         * zhy的okhttp网络框架
         * 注册OkHttp请求http请求
         * 自己主动取消的错误的 java.net.SocketException: Socket closed
         * 超时的错误是 java.net.SocketTimeoutException
         * 网络出错的错误是java.net.ConnectException: Failed to connect to xxxxx
         */
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggerInterceptor("TAG"))
                .addInterceptor(new UuidTokenInterceptor(context))
                //设置连接的超时时间
                .connectTimeout(5000L, TimeUnit.MILLISECONDS)
                //设置读的超时时间
                .readTimeout(5000L, TimeUnit.MILLISECONDS)
                //设置写的超时时间
                .writeTimeout(5000L, TimeUnit.SECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * 这是最关键的函数，当程序中有未捕获的异常，系统将会自动调用这个方法，
     * thread为出现未捕获异常的线程，ex为为未捕获的异常，有了这个ex，我们就可以得到
     * 异常信息了
     *
     * @param thread
     * @param ex
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        //导出异常信息到SD卡中
        if (SAVESDCARD) {
            try {
                dumpExceptionToSDCard(ex);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        //上传异常信息到服务器，便于开发人员分析日志从而解决bug
        uploadExceptionToServer(thread, ex);
        //跳转到错误界面
        final String msg = getCrashInfo(ex);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                try {
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setAction(mContext.getPackageName() + ".NOTIFY_ERROR");
                    String ss = mContext.getPackageName();
                    Log.i(TAG, "run: "+ss);

                    intent.putExtra("msg", msg);
                    intent.putExtra("APPID", APPID);
                    intent.putExtra("Url", Url);
                    intent.putExtra("projectName", projectName);
                    mContext.startActivity(intent);
                    CompleteQuit.getInstance().exitAll(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Looper.loop();
            }
        }.start();
        ex.printStackTrace();
        //如果系统提供了默认的异常处理器，则交给系统去结束程序，否则就由自己结束自己
        if (mDefaultCrasHandler != null) {
            mDefaultCrasHandler.uncaughtException(thread, ex);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    /**
     * 把错误信息保存到本地sd卡中
     *
     * @param ex
     * @throws IOException
     */
    private void dumpExceptionToSDCard(Throwable ex) throws IOException {
        //如果SD卡不存在或无法使用，则无法把异常信息写入SD卡中
        if (!Environment.getExternalStorageState()
                .equals(Environment.MEDIA_MOUNTED)) {
            if (DEBUG) {
                Log.i(TAG, "dumpExceptionToSDCard: SD卡不存在");
                return;
            }
        }
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(new Date(current));
        File file = new File(PATH + FILE_NAME);
        //先删除之前的异常信息
        if (file.exists()) {
            DeleteFile(file);
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        try {
            PrintWriter pw = new PrintWriter(
                    new BufferedWriter(
                            new FileWriter(file + File.separator
                                    + time + FILE_NAME_SUFFIX)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            ex.printStackTrace(pw);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "dumpExceptionToSDCard: " + e);
        }
    }

    /**
     * 写入手机的基本信息
     *
     * @param pw
     */
    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print('_');
        pw.println(pi.versionCode);

        //Android版本号
        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print('_');
        pw.println(Build.VERSION.SDK_INT);

        //手机制造商
        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        //手机型号
        pw.print("Model: ");
        pw.println(Build.MODEL);

        //CUP架构
        pw.print("CUP ABI: ");
        pw.println(Build.CPU_ABI);

    }


    /**
     * 向result服务器发送错误日志
     */
    private void uploadExceptionToServer(final Thread thread, final Throwable ex) {
        Throwable cause = ex.getCause();
        StackTraceElement[] stackTrace = ex.getStackTrace();
        String error_text = "错误：" + ex.toString() + "  \n  ";
        for (int i = 0; i < stackTrace.length; i++) {
            error_text += stackTrace[i].getFileName() + " class:"
                    + stackTrace[i].getClassName() + " method:"
                    + stackTrace[i].getMethodName() + " line:"
                    + stackTrace[i].getLineNumber() + "  \n  ";
        }
    }


    /**
     * 获取错误信息
     *
     * @param ex
     * @return
     */
    private String getCrashInfo(Throwable ex) {
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }

        String result = info.toString();
        printWriter.close();
        mDeviceCrashInfo.put(STACK_TRACE, result);
        return mDeviceCrashInfo.toString();
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param file 要删除的根目录
     */
    public void DeleteFile(File file) {
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
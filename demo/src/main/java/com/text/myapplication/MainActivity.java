package com.text.myapplication;

import android.Manifest;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.text.updatademo.listenter.OnDownAPKListenter;
import com.text.updatademo.utils.UpdateManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_error).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = 5 / 0;
            }
        });
        findViewById(R.id.btn_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如需适配7.0，则要动态申请读写SD卡的权限
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    initUpdate();
                } else {
                    if (PermissionsUtil.hasPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        initUpdate();
                    } else {
                        PermissionsUtil.requestPermission(MainActivity.this, new PermissionListener() {
                            @Override
                            public void permissionGranted(@NonNull String[] permissions) {
                                initUpdate();
                            }

                            @Override
                            public void permissionDenied(@NonNull String[] permissions) {
                                Toast.makeText(MainActivity.this, "无此权限可能会导致系统更新后不能安装", Toast.LENGTH_SHORT).show();
                                //TODO:无权限就不更新了

                            }
                        }, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE});
                    }
                }
            }
        });
    }

    private void initUpdate() {
        UpdateManager.getInstance()
                .initUpdateMsg(this,
                        Constants.APPID,
                        Constants.COMMON_URL,
                        "XXX")
                .setOnDownAPKListenter(new OnDownAPKListenter() {
                    @Override
                    public void onCancel() {
                        //取消下载
                        Toast.makeText(MainActivity.this, "取消下载", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

# UpDataDemo

一个关于下载的库，可以适配7.0下载后安装，该库供内部使用，如需外部使用，则建议导入library来自己定制。

# 更新的用法
1.先配置，在你的项目的build.gradle中配置

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
			}
		    }
  
  
 2.然后依赖，在当前moudle的build.gradle里面添加
 
	dependencies {
		compile 'com.github.liuxinggen:UpDataDemo:1.0'
		}
 3.代码中应用
 
	参数说明：
	context     上下文
	APPID       该产品对应的APPID
	HttpUrl     该产品的url地址，列如：http://192.168.1.1:8099/
	projectName 该产品的名称，列如：智慧农业
 
 	 UpdateManager.getInstance()
                .initUpdateMsg(context,APPID,HttpUrl,projectName)
                .setOnDownAPKListenter(new OnDownAPKListenter() {
                    @Override
                    public void onCancel() {
                        //取消下载
                       
                    }
                });
4.适配7.0下载后的安装，需要在AndroidManifest.xml中配置

	<!-- 适配android7.0以上无法安装，请在AndroidManifest.xml中添加标签 -->
        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>
5.另外记得在代码中动态申请对SD卡的读写操作昂！！！



# UpDataDemo

一个关于下载的库，可以适配7.0下载后安装，该库供内部使用，如需外部使用，则建议导入library来自己定制。

# 错误收集容器

 在你自己的Application中配置：
 
 	 CrashHandler crashHandler = CrashHandler.getsInstance();
         crashHandler.init(getApplicationContext(),“appid”,"错误上传url","产品名称");
		
# 更新用发

 在你需要更新的地方配置：
  
  	UpdateManager.getInstance()
                .initUpdateMsg(this,“appid”,url,"产品名称")
                .setOnDownAPKListenter(new OnDownAPKListenter() {
                    @Override
                    public void onCancel() {
                        //取消下载
                        Toast.makeText(MainActivity.this, "取消下载", Toast.LENGTH_SHORT).show();
                    }
                });
		
# 注：

	url为当前服务端配置的域名，如：http://211.149.244.64:8099/即可。appid为每个App对应一个appid	



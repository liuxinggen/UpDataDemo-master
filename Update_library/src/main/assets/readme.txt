一个关于下载的库，可以适配7.0下载后安装，该库供内部使用，如需外部使用，则建议导入library来自己定制。


# 错误收集容器

 
在你自己的Application中配置：
 
 	 
CrashHandler crashHandler = CrashHandler.getsInstance();
     
crashHandler.init(getApplicationContext(),“appid”,"url","产品名称");
		


# 更新用发

 
在你需要更新的地方配置：
  
  	
UpdateManager.getInstance().initUpdateMsg(this,“appid”,"url","产品名称")
.setOnDownAPKListenter(new OnDownAPKListenter() {
                     
@Override
                    
public void onCancel() {
                        
//取消下载
                        
Toast.makeText(MainActivity.this, "取消下载", Toast.LENGTH_SHORT).show();
                    
}
                
});
һ���������صĿ⣬��������7.0���غ�װ���ÿ⹩�ڲ�ʹ�ã������ⲿʹ�ã����鵼��library���Լ����ơ�


# �����ռ�����

 
�����Լ���Application�����ã�
 
 	 
CrashHandler crashHandler = CrashHandler.getsInstance();
     
crashHandler.init(getApplicationContext(),��appid��,"url","��Ʒ����");
		


# �����÷�

 
������Ҫ���µĵط����ã�
  
  	
UpdateManager.getInstance().initUpdateMsg(this,��appid��,"url","��Ʒ����")
.setOnDownAPKListenter(new OnDownAPKListenter() {
                     
@Override
                    
public void onCancel() {
                        
//ȡ������
                        
Toast.makeText(MainActivity.this, "ȡ������", Toast.LENGTH_SHORT).show();
                    
}
                
});
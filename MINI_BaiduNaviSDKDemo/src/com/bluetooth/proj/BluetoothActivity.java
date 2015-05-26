package com.bluetooth.proj;
import java.io.IOException;  
import java.io.InputStream;  
import java.io.OutputStream;  
import java.util.ArrayList;  
import java.util.List;  
import java.util.UUID;  

import com.baidu.navi.sdkdemo.Data;
import com.baidu.navi.sdkdemo.R;
import com.baidu.navisdk.util.SysOSAPI;
  
import android.app.Activity;  
import android.bluetooth.BluetoothAdapter;  
import android.bluetooth.BluetoothDevice;  
import android.bluetooth.BluetoothServerSocket;  
import android.bluetooth.BluetoothSocket;  
import android.content.Context;  
import android.os.Bundle;  
import android.os.Handler;  
import android.os.Message;  
import android.util.Log;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.view.inputmethod.InputMethodManager;  

import android.widget.ArrayAdapter;  
import android.widget.Button;  
import android.widget.EditText;  
import android.widget.ListView;  
import android.widget.Toast;  
import android.widget.AdapterView.OnItemClickListener;  
  
public class BluetoothActivity extends  Activity{  
      
    /* 一些常量，代表服务器的名称 */  
    public static final String PROTOCOL_SCHEME_RFCOMM = "btspp";  
      
    private ListView mListView;  
    private Button sendButton;  
    private Button disconnectButton; 
    private Button naviInfoSend;//自动发送导航信息的按钮
    private Button stopNavi;//停止自动导航的按钮
    private EditText editMsgView;  
    private ArrayAdapter<String> mAdapter;  
    private List<String> msgList=new ArrayList<String>();  
    Context mContext;  
    
    Handler handler;
    private BluetoothServerSocket mserverSocket = null;  
    private ServerThread startServerThread = null;  
    private clientThread clientConnectThread = null;  
    private BluetoothSocket socket = null;  
    private BluetoothDevice device = null;  
    private readThread mreadThread = null;;   
    private BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();  
      
        @Override  
        public void onCreate(Bundle savedInstanceState) {  
            super.onCreate(savedInstanceState);   
            setContentView(R.layout.chat);  
            mContext = this;  
            init();  
        }  
      
        private void init(){            
              
            mAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, msgList);  
            mListView = (ListView) findViewById(R.id.list);  
            mListView.setAdapter(mAdapter);  
            mListView.setFastScrollEnabled(true);  
            editMsgView= (EditText)findViewById(R.id.MessageText);    
            editMsgView.clearFocus();  
            //发送导航信息的线程
            final Thread naviSendthread= new Thread(new Runnable() {
				public void run() {
					
					System.out.println("进入了线程中运行");
					
					
					String s = Data.getNaviInfo();
					String afterSubNaviInfo = subNaviInfo(s);
					
					List<String> list = getNaviInfoForming(afterSubNaviInfo);
					System.out.println("开始发送导航信息~");
					for(int i=4;i<list.size();i++)
					{
						list.remove(i);
							
					}
					for(int i=0;i<list.size();i++)
					{
						System.out.println("导航信息是："+list.get(i));
					}
					
					OutputStream os = null;
					try {
						os = socket.getOutputStream();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					//发送导航指令信息
					for(int i=0;i<Integer.parseInt(list.get(1));i++)
					{
						if (socket == null)   
			            {  		
							
			                return;  
			            }  
			            try {                 
			                   
			                os.write(list.get(0).getBytes());
			                
			                Message msg = new Message();
	                        msg.obj = list.get(0);
	                        msg.what = 0;
			                handler.sendMessage(msg);
			               
			                Thread.currentThread().sleep(10000);
			                
			                
			                System.out.println("运行到这里了,这是第"+i+"次运行");
			            } catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			            
					}
					
					if (socket == null)   
		            {  			                
		                return;  
		            }  
		            try {                 
		                 
		                os.write(list.get(2).getBytes());
		                Message msg = new Message();
                        msg.obj = list.get(2);
                        msg.what = 0;
		                handler.sendMessage(msg);
		                Thread.currentThread().sleep(10000);
		            } catch (IOException e) {  
		                e.printStackTrace();  
		            } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
						
					for(int i=0;i<Integer.parseInt(list.get(3));i++)
					{
						if (socket == null)   
			            {  			                
			                return;  
			            }  
			            try {                 
			                 
			                os.write(list.get(0).getBytes());//因发送的是前进指令，可用第一个动作指令符号替代
			                Message msg = new Message();
	                        msg.obj = list.get(0);
	                        msg.what = 0;
			                handler.sendMessage(msg);
			                Thread.currentThread().sleep(10000);
			            } catch (IOException e) {  
			                e.printStackTrace();  
			            } catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
					}
					System.out.println("导航信息发送结束");
					
					
				}
			});
            
            handler = new Handler() {
                public void handleMessage(Message msg) {
                	switch (msg.what) {
                	case 0:
                	   System.out.println("我在更新界面");
                	   msgList.add((String) msg.obj); 
                	   System.out.println("msgList:"+(String) msg.obj);
                       mAdapter.notifyDataSetChanged();  
                       mListView.setSelection(msgList.size() - 1);
                    
                   }
                }

            };
            //自动发送导航信息
            naviInfoSend = (Button)findViewById(R.id.btn_NaviInfo_send);
            naviInfoSend.setOnClickListener(new OnClickListener() {			
            	@Override
            	public void onClick(View v) {
					
					naviSendthread.start();
				}
			});
           //停止发送导航信息
            stopNavi = (Button)findViewById(R.id.btn_stopNavi);
            stopNavi.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					try {
						Thread.currentThread().wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
            		
            		
            		
            sendButton= (Button)findViewById(R.id.btn_msg_send);  
            sendButton.setOnClickListener(new OnClickListener() {  
                @Override  
                public void onClick(View arg0) {  
                  
                    String msgText =editMsgView.getText().toString();  
                    if (msgText.length()>0) {  
                        sendMessageHandle(msgText);   
                        editMsgView.setText("");  
                        editMsgView.clearFocus();  
                        //close InputMethodManager  
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);   
                        imm.hideSoftInputFromWindow(editMsgView.getWindowToken(), 0);  
                    }else  
                    Toast.makeText(mContext, "发送内容不能为空！", Toast.LENGTH_SHORT).show();  
                }  
            });  
              
            disconnectButton= (Button)findViewById(R.id.btn_disconnect);  
            disconnectButton.setOnClickListener(new OnClickListener() {  
                @Override  
                public void onClick(View arg0) {  
                    // TODO Auto-generated method stub  
                    if (BluetoothMsg.serviceOrCilent == BluetoothMsg.ServerOrCilent.CILENT)   
                    {  
                        shutdownClient();  
                    }  
                    else if (BluetoothMsg.serviceOrCilent == BluetoothMsg.ServerOrCilent.SERVICE)   
                    {  
                        shutdownServer();  
                    }  
                    BluetoothMsg.isOpen = false;  
                    BluetoothMsg.serviceOrCilent=BluetoothMsg.ServerOrCilent.NONE;  
                    Toast.makeText(mContext, "已断开连接！", Toast.LENGTH_SHORT).show();  
                }  
            });       
        }      
  
        private Handler LinkDetectedHandler = new Handler() {  
            @Override  
            public void handleMessage(Message msg) {  
                //Toast.makeText(mContext, (String)msg.obj, Toast.LENGTH_SHORT).show();  
                if(msg.what==1)  
                {  
                    msgList.add((String)msg.obj);  
                }  
                else  
                {  
                    msgList.add((String)msg.obj);  
                }  
                mAdapter.notifyDataSetChanged();  
                mListView.setSelection(msgList.size() - 1);  
            }  
        };      
          
    @Override  
    protected void onResume() {  
          
        BluetoothMsg.serviceOrCilent=BluetoothMsg.ServerOrCilent.CILENT;  
          
         if(BluetoothMsg.isOpen)  
            {  
                Toast.makeText(mContext, "连接已经打开，可以通信。如果要再建立连接，请先断开！", Toast.LENGTH_SHORT).show();  
                return;  
            }  
            if(BluetoothMsg.serviceOrCilent==BluetoothMsg.ServerOrCilent.CILENT)  
            {  
                String address = BluetoothMsg.BlueToothAddress;  
                if(!address.equals("null"))  
                {  
                    device = mBluetoothAdapter.getRemoteDevice(address);      
                    clientConnectThread = new clientThread();  
                    clientConnectThread.start();  
                    BluetoothMsg.isOpen = true;  
                }  
                else  
                {  
                    Toast.makeText(mContext, "address is null !", Toast.LENGTH_SHORT).show();  
                }  
            }  
            else if(BluetoothMsg.serviceOrCilent==BluetoothMsg.ServerOrCilent.SERVICE)  
            {                     
                startServerThread = new ServerThread();  
                startServerThread.start();  
                BluetoothMsg.isOpen = true;  
            }  
        super.onResume();  
    }  
  
    //开启客户端  
        private class clientThread extends Thread {           
            @Override  
            public void run() {  
                try {  
                    //创建一个Socket连接：只需要服务器在注册时的UUID号  
                     //socket = device.createRfcommSocketToServiceRecord(BluetoothProtocols.OBEX_OBJECT_PUSH_PROTOCOL_UUID);  
                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));  
                    //连接  
                    Message msg2 = new Message();  
                    msg2.obj = "请稍候，正在连接服务器:"+BluetoothMsg.BlueToothAddress;  
                    msg2.what = 0;  
                    LinkDetectedHandler.sendMessage(msg2);  
                      
                    socket.connect();  
                      
                    Message msg = new Message();  
                    msg.obj = "已经连接上服务端！可以发送信息。";  
                    msg.what = 0;  
                    LinkDetectedHandler.sendMessage(msg);  
                    //启动接受数据  
                    mreadThread = new readThread();  
                    mreadThread.start();  
                }   
                catch (IOException e)   
                {  
                    Log.e("connect", "", e);  
                    Message msg = new Message();  
                    msg.obj = "连接服务端异常！断开连接重新试一试。";  
                    msg.what = 0;  
                    LinkDetectedHandler.sendMessage(msg);  
                }   
            }  
        };  
  
        //开启服务器  
        private class ServerThread extends Thread {   
            @Override  
            public void run() {  
                          
                try {  
                    /* 创建一个蓝牙服务器  
                     * 参数分别：服务器名称、UUID   */   
                    mserverSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(PROTOCOL_SCHEME_RFCOMM,  
                            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));         
                      
                    Log.d("server", "wait cilent connect...");  
                      
                    Message msg = new Message();  
                    msg.obj = "请稍候，正在等待客户端的连接...";  
                    msg.what = 0;  
                    LinkDetectedHandler.sendMessage(msg);  
                      
                    /* 接受客户端的连接请求 */  
                    socket = mserverSocket.accept();  
                    Log.d("server", "accept success !");  
                      
                    Message msg2 = new Message();  
                    String info = "客户端已经连接上！可以发送信息。";  
                    msg2.obj = info;  
                    msg.what = 0;  
                    LinkDetectedHandler.sendMessage(msg2);  
                    //启动接受数据  
                    mreadThread = new readThread();  
                    mreadThread.start();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        };  
        /* 停止服务器 */  
        private void shutdownServer() {  
            new Thread() {  
                @Override  
                public void run() {  
                    if(startServerThread != null)  
                    {  
                        startServerThread.interrupt();  
                        startServerThread = null;  
                    }  
                    if(mreadThread != null)  
                    {  
                        mreadThread.interrupt();  
                        mreadThread = null;  
                    }                 
                    try {                     
                        if(socket != null)  
                        {  
                            socket.close();  
                            socket = null;  
                        }  
                        if (mserverSocket != null)  
                        {  
                            mserverSocket.close();/* 关闭服务器 */  
                            mserverSocket = null;  
                        }  
                    } catch (IOException e) {  
                        Log.e("server", "mserverSocket.close()", e);  
                    }  
                };  
            }.start();  
        }  
        /* 停止客户端连接 */  
        private void shutdownClient() {  
            new Thread() {  
                @Override  
                public void run() {  
                    if(clientConnectThread!=null)  
                    {  
                        clientConnectThread.interrupt();  
                        clientConnectThread= null;  
                    }  
                    if(mreadThread != null)  
                    {  
                        mreadThread.interrupt();  
                        mreadThread = null;  
                    }  
                    if (socket != null) {  
                        try {  
                            socket.close();  
                        } catch (IOException e) {  
                            // TODO Auto-generated catch block  
                            e.printStackTrace();  
                        }  
                        socket = null;  
                    }  
                };  
            }.start();  
        }  
          
        //发送数据  
        private void sendMessageHandle(String msg)    
        {         
            if (socket == null)   
            {  
                Toast.makeText(mContext, "没有连接", Toast.LENGTH_SHORT).show();  
                return;  
            }  
            try {                 
                OutputStream os = socket.getOutputStream();   
                os.write(msg.getBytes());               
            } catch (IOException e) {  
                e.printStackTrace();  
            }             
            msgList.add(msg);  
            mAdapter.notifyDataSetChanged();  
            mListView.setSelection(msgList.size() - 1);  
        }  
        //读取数据  
        private class readThread extends Thread {   
            @Override  
            public void run() {  
                  
                byte[] buffer = new byte[1024];  
                int bytes;  
                InputStream mmInStream = null;  
                  
                try {  
                    mmInStream = socket.getInputStream();  
                } catch (IOException e1) {  
                    // TODO Auto-generated catch block  
                    e1.printStackTrace();  
                }     
                while (true) {  
                    try {  
                        // Read from the InputStream  
                        if( (bytes = mmInStream.read(buffer)) > 0 )  
                        {  
                            byte[] buf_data = new byte[bytes];  
                            for(int i=0; i<bytes; i++)  
                            {  
                                buf_data[i] = buffer[i];  
                            }  
                            String s = new String(buf_data);  
                            Message msg = new Message();  
                            msg.obj = s;  
                            msg.what = 1;  
                            LinkDetectedHandler.sendMessage(msg);  
                        }  
                    } catch (IOException e) {  
                        try {  
                            mmInStream.close();  
                        } catch (IOException e1) {  
                            // TODO Auto-generated catch block  
                            e1.printStackTrace();  
                        }  
                        break;  
                    }  
                }  
            }  
        }  
        @Override  
        protected void onDestroy() {  
            super.onDestroy();  
  
            if (BluetoothMsg.serviceOrCilent == BluetoothMsg.ServerOrCilent.CILENT)   
            {  
                shutdownClient();  
            }  
            else if (BluetoothMsg.serviceOrCilent == BluetoothMsg.ServerOrCilent.SERVICE)   
            {  
                shutdownServer();  
            }  
            BluetoothMsg.isOpen = false;  
            BluetoothMsg.serviceOrCilent = BluetoothMsg.ServerOrCilent.NONE;  
        }  
        /**
    	 * 
    	 * 导航关键信息截取
    	 * @param str
    	 * @return
    	 */
    	public static String subNaviInfo(String str)
    	{
    		
    		char[] sarray = str.toCharArray();//字符串转换为字符数组
    		int deleteIndexStart = 0;//导航信息截取的起始位（导航信息前）
    		int deleteIndexEnd = 0;//导航信息截取的结束位（导航信息后）
    		for(int i=0;i<sarray.length;i++)
    		{
    			if(sarray[i]=='始')
    			{
    				
    				deleteIndexStart = i+2;
    			}
    			if(sarray[i]=='结')
    			{
    				
    				deleteIndexEnd = i-3;
    			}
    		}
    		String afterSub = str.substring(deleteIndexStart,deleteIndexEnd);//导航关键信息提取		
    		return afterSub;
    	}
    	/**
    	 * 当行信息格式化提取
    	 */
    	public static List<String> getNaviInfoForming(String afterSubNaviInfo)
    	{
    		char[] sarray = afterSubNaviInfo.toCharArray();//字符串转换为字符数组
    		String action1 = "a";//首个前进命令
    		String action = "";//暂存动作指令的数据结构
    		String distense = "";//暂存行进距离的数据结构
    		List<String> formingNaviInfo = new ArrayList<String>();
    		formingNaviInfo.add(action1);
    		for(int i=0;i<sarray.length;i++)
    		{
    			switch (sarray[i]) {
    			case '一':
    				
    				distense+="1";
    				break;
    			case '两':
    				distense+="2";
    				break;
    			case '二':
    				distense+="2";
    				break;
    			case '三':
    				distense+="3";
    				break;
    			case '四':
    				distense+="4";
    				break;
    			case '五':
    				distense+="5";
    				break;
    			case '六':
    				distense+="6";
    				break;
    			case '七':
    				distense+="7";
    				break;
    			case '八':
    				distense+="8";
    				break;
    			case '九':
    				distense+="9";
    				break;
    			case '十':
    				distense+="0";
    				break;
    			case '百':
    				if(sarray[i+1]=='一'||sarray[i+1]=='二'||sarray[i+1]=='三'||sarray[i+1]=='四'||sarray[i+1]=='五'||sarray[i+1]=='六'||sarray[i+1]=='七'||sarray[i+1]=='八'||sarray[i+1]=='九')
    				{
    					continue;
    				}else {
    					distense+="00";
					}
    				
    				break;
    			case '千':
    				if(sarray[i+1]=='一'||sarray[i+1]=='二'||sarray[i+1]=='三'||sarray[i+1]=='四'||sarray[i+1]=='五'||sarray[i+1]=='六'||sarray[i+1]=='七'||sarray[i+1]=='八'||sarray[i+1]=='九')
    				{
    					continue;
    				}else {
    					distense+="000";
					}
    				
    				break;
    			case '左':
    				action+="b";
    				break;
    			case '右':				
    				action+="c";
    				break;
    			default:
    				if(!distense.equals(""))
    				{
    					formingNaviInfo.add(distense);
    				}
    				if(!action.equals(""))
    				{
    					formingNaviInfo.add(action);
    				}
    				distense="";
    				action="";
    				break;
    			}
    		}
    		return formingNaviInfo;
    	}
          
}  
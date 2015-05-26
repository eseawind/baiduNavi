package com.bluetooth.proj;
import java.util.ArrayList;  
import java.util.Iterator;  
import java.util.List;  
import java.util.Set;  

import com.baidu.navi.sdkdemo.R;
  
import android.app.Activity;  
import android.app.AlertDialog;  
import android.bluetooth.BluetoothAdapter;  
import android.bluetooth.BluetoothDevice;  
import android.content.BroadcastReceiver;  
import android.content.Context;  
import android.content.DialogInterface;  
import android.content.Intent;  
import android.content.IntentFilter;  
import android.os.Bundle;  
import android.util.Log;  
import android.view.View;  
import android.view.View.OnClickListener;  
import android.widget.AdapterView;  
import android.widget.AdapterView.OnItemClickListener;  
import android.widget.ArrayAdapter;  
import android.widget.Button;  
import android.widget.ListView; 
public class SearchDeviceActivity extends Activity implements OnItemClickListener{
	private BluetoothAdapter blueadapter=null;  
    private DeviceReceiver mydevice=new DeviceReceiver();  
    private List<String> deviceList=new ArrayList<String>();  
    private ListView deviceListview;  
    private Button btserch;  
    private ArrayAdapter<String> adapter;  
    private boolean hasregister=false;  
      
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.finddevice);  
        setView();  
        setBluetooth();  
          
    }  
      
    private void setView(){  
      
        deviceListview=(ListView)findViewById(R.id.devicelist);  
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceList);  
        deviceListview.setAdapter(adapter);  
        deviceListview.setOnItemClickListener(this);  
        btserch=(Button)findViewById(R.id.start_seach);  
        btserch.setOnClickListener(new ClinckMonitor());  
          
    }  
  
    @Override  
    protected void onStart() {  
        //注册蓝牙接收广播  
        if(!hasregister){  
            hasregister=true;  
            IntentFilter filterStart=new IntentFilter(BluetoothDevice.ACTION_FOUND);      
            IntentFilter filterEnd=new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);      
            registerReceiver(mydevice, filterStart);  
            registerReceiver(mydevice, filterEnd);  
        }         
        super.onStart();  
    }  
  
    @Override  
    protected void onDestroy() {  
        if(blueadapter!=null&&blueadapter.isDiscovering()){  
            blueadapter.cancelDiscovery();  
        }  
        if(hasregister){  
            hasregister=false;  
            unregisterReceiver(mydevice);  
        }  
        super.onDestroy();  
    }  
    /** 
     * Setting Up Bluetooth 
     */  
    private void setBluetooth(){  
         blueadapter=BluetoothAdapter.getDefaultAdapter();  
           
            if(blueadapter!=null){  //Device support Bluetooth  
                //确认开启蓝牙  
                if(!blueadapter.isEnabled()){  
                    //请求用户开启  
                    Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
                    startActivityForResult(intent, RESULT_FIRST_USER);  
                    //使蓝牙设备可见，方便配对  
                    Intent in=new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);  
                    in.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 200);  
                    startActivity(in);  
                    //直接开启，不经过提示  
                    blueadapter.enable();  
                }  
            }  
            else{   //Device does not support Bluetooth  
                  
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);  
                dialog.setTitle("No bluetooth devices");  
                dialog.setMessage("Your equipment does not support bluetooth, please change device");  
                  
                dialog.setNegativeButton("cancel",  
                        new DialogInterface.OnClickListener() {  
                            @Override  
                            public void onClick(DialogInterface dialog, int which) {  
                                  
                            }  
                        });  
                dialog.show();  
            }  
    }  
      
    /** 
     * Finding Devices 
     */  
    private void findAvalibleDevice(){  
        //获取可配对蓝牙设备  
        Set<BluetoothDevice> device=blueadapter.getBondedDevices();  
          
        if(blueadapter!=null&&blueadapter.isDiscovering()){  
            deviceList.clear();  
            adapter.notifyDataSetChanged();  
        }  
        if(device.size()>0){ //存在已经配对过的蓝牙设备  
            for(Iterator<BluetoothDevice> it=device.iterator();it.hasNext();){  
                BluetoothDevice btd=it.next();  
                deviceList.add(btd.getName()+'\n'+btd.getAddress());  
                adapter.notifyDataSetChanged();  
            }  
        }else{  //不存在已经配对过的蓝牙设备  
            deviceList.add("No can be matched to use bluetooth");  
            adapter.notifyDataSetChanged();  
        }  
    }  
      
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
          
        switch(resultCode){  
        case RESULT_OK:  
            findAvalibleDevice();  
            break;  
        case RESULT_CANCELED:  
            break;  
        }  
        super.onActivityResult(requestCode, resultCode, data);  
    }  
  
    private class ClinckMonitor implements OnClickListener{  
  
        @Override  
        public void onClick(View v) {  
            if(blueadapter.isDiscovering()){  
                blueadapter.cancelDiscovery();  
                btserch.setText("repeat search");  
            }else{  
                findAvalibleDevice();  
                blueadapter.startDiscovery();  
                btserch.setText("stop search");  
            }  
        }  
    }  
    /** 
     * 蓝牙搜索状态广播监听 
     * @author Andy 
     * 
     */  
    private class DeviceReceiver extends BroadcastReceiver{  
  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            String action =intent.getAction();  
            if(BluetoothDevice.ACTION_FOUND.equals(action)){    //搜索到新设备  
                BluetoothDevice btd=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);  
                //搜索没有配过对的蓝牙设备  
                 if (btd.getBondState() != BluetoothDevice.BOND_BONDED) {  
                     deviceList.add(btd.getName()+'\n'+btd.getAddress());  
                     adapter.notifyDataSetChanged();  
                 }  
            }  
             else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){   //搜索结束  
                   
                    if (deviceListview.getCount() == 0) {  
                        deviceList.add("No can be matched to use bluetooth");  
                        adapter.notifyDataSetChanged();  
                    }  
                    btserch.setText("repeat search");  
                }  
        }     
    }  
  
    @Override  
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {  
          
        Log.e("msgParent", "Parent= "+arg0);  
        Log.e("msgView", "View= "+arg1);  
        Log.e("msgChildView", "ChildView= "+arg0.getChildAt(pos-arg0.getFirstVisiblePosition()));  
          
            final String msg = deviceList.get(pos);  
              
            if(blueadapter!=null&&blueadapter.isDiscovering()){  
                blueadapter.cancelDiscovery();  
                btserch.setText("repeat search");  
            }  
              
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);// 定义一个弹出框对象  
            dialog.setTitle("Confirmed connecting device");  
            dialog.setMessage(msg);  
            dialog.setPositiveButton("connect",  
                    new DialogInterface.OnClickListener() {  
                        @Override  
                        public void onClick(DialogInterface dialog, int which) {  
                            BluetoothMsg.BlueToothAddress=msg.substring(msg.length()-17);  
                              
                            if(BluetoothMsg.lastblueToothAddress!=BluetoothMsg.BlueToothAddress){  
                                BluetoothMsg.lastblueToothAddress=BluetoothMsg.BlueToothAddress;  
                            }  
                              
                            Intent in=new Intent(SearchDeviceActivity.this,BluetoothActivity.class);  
                            startActivity(in);  
                          
                        }  
                    });  
            dialog.setNegativeButton("cancel",  
                    new DialogInterface.OnClickListener() {  
                        @Override  
                        public void onClick(DialogInterface dialog, int which) {  
                            BluetoothMsg.BlueToothAddress = null;  
                        }  
                    });  
            dialog.show();  
    }  
}

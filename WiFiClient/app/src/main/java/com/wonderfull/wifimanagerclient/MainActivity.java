package com.wonderfull.wifimanagerclient;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    public  UDPClient m_UDPClient;
    public  Button bt_send_check_device;
    public  Button bt_send_set_wifi;

    private final MessageHandler mHandler = new MessageHandler(this);
    public  static final String   Debug_Tag ="[WiFiManager]";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ////////////////////////////////////////////////////////////////////////
        bt_send_check_device =(Button)findViewById(R.id.bt_send_check_device) ;
        bt_send_check_device.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                sendCheckDevice();
            }

        });


        bt_send_set_wifi =(Button)findViewById(R.id.bt_send_set_wifi) ;
        bt_send_set_wifi.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View view){
                sendSetWiFi();
            }

        });

        ////////////////////////////////////////////////////////////////////////

    }
    public void handleMessage(Message msg){
        String sendMessage = msg.getData().getString("SendMessage");
        Toast.makeText(getApplicationContext(), sendMessage,Toast.LENGTH_LONG).show();

        String receivedMessage = msg.getData().getString("ReceivedMessage");
        Toast.makeText(getApplicationContext(), receivedMessage,Toast.LENGTH_LONG).show();
    }
    public void sendCheckDevice(){
        /*
        "message_name" :"check_device"
        “device_name” : "uopluse" ,
        “controller_information” : [
        {
        “controller_name” : "uopluse_mobile"
        }
        ]
        }
        */
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonSubObject = new JSONObject();
        String strMessage;
        //JSONArray jsonArray = new JSONArray();
        try {
            jsonObject.put("message_name","check_device");
            jsonObject.put("device_name","uopluse");
            jsonObject.put("controller_information",jsonSubObject);

            jsonSubObject.put("controller_name","uopluse_mobile");
            strMessage = jsonObject.toString();
            Log.v(Debug_Tag,"made json message : " + strMessage);

            if(m_UDPClient == null)
            {
                m_UDPClient = new UDPClient();
                m_UDPClient.sendData(strMessage);
            }else{
                m_UDPClient.sendData(strMessage);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    public void sendSetWiFi(){
        /*
        {
        "message_name":"set_wifi",
        "device_name":"uopluse",
        "controller_information":{
        "controller_name":"uopluse_mobile"
        }
        {
        "ssid" :"0000000",
        "password" : "0000000" ,
        "security_type" : "0000000"
        }
        }
        */
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonSubObject = new JSONObject();
        JSONObject jsonWiFiObject = new JSONObject();
        JSONObject jsonWiFiSubObject = new JSONObject();
        String strMessage;
        //JSONArray jsonArray = new JSONArray();
        try {

            jsonObject.put("message_name","set_wifi");
            jsonObject.put("device_name","uopluse");

            jsonObject.put("controller_information",jsonSubObject);
            jsonSubObject.put("controller_name","uopluse_mobile");

            jsonObject.put("wifi_information",jsonWiFiSubObject);
            jsonWiFiSubObject.put("ssid","1thefull_6f_2g_robotTeam");
            jsonWiFiSubObject.put("password","1thefull322!");
            jsonWiFiSubObject.put("security_type","wpa2");
            /*
            jsonObject.put("message_name","set_wifi");
            jsonObject.put("device_name","uopluse");
            jsonObject.put("controller_information",jsonSubObject);
            jsonSubObject.put("controller_name","uopluse_mobile");

            jsonSubObject.put("wifi_information",jsonWiFiObject);
            jsonWiFiObject.put("ssid","1thefull_6f_2g_robotTeam");
            jsonWiFiObject.put("password","1thefull322!");
            jsonWiFiObject.put("security_type","wpa2");
            */


            strMessage = jsonObject.toString();
            Log.v(Debug_Tag,"made json message : " + strMessage);

            if(m_UDPClient == null)
            {
                m_UDPClient = new UDPClient();
                m_UDPClient.sendData(strMessage);
            }else{
                m_UDPClient.sendData(strMessage);
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    // Class
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private static class MessageHandler extends Handler{
        private final WeakReference<MainActivity> mActivity;
        public  static final String   Debug_Tag ="[WiFiManager]";

        public MessageHandler(MainActivity activity)
        {
            mActivity = new WeakReference<MainActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg){
            MainActivity activity = mActivity.get();
            if(activity != null)
            {
                Log.v(Debug_Tag,"MessageHandler.handleMessage ! ");
                activity.handleMessage(msg);
            }
        }
    }
    class UDPClient {

        public  static final String   Debug_Tag ="[WiFiManager]";
        public DatagramPacket dp= null, packet;
        public DatagramSocket ds= null;
        public  int CLIENT_PORT = 2020;
        InetAddress ia;
        int port;

        public UDPClient(){
            ///////////////
            try {
                ds = new DatagramSocket();
                ds.setSoTimeout(10000);
                ds.setBroadcast(true); // enable broadcast

                Log.v(Debug_Tag,"UDPClient was created ! ");

            }catch (SocketException e) {
                Log.v(Debug_Tag,"UDPClient: Error\n" + e);
                e.printStackTrace();
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void sendData(String sendMessage){

            String str = sendMessage;
            byte[] s = str.getBytes();

            try{
                StrictMode.ThreadPolicy policy = new   StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);

                packet = new DatagramPacket(s, s.length,  getBroadcastAddress(), CLIENT_PORT);
                ds.send(packet);

                //Wait for a response

                byte[] recvBuf = new byte[15000];
                DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
                ds.receive(receivePacket);

                String message = new String(receivePacket.getData()).trim();
                Log.v(Debug_Tag,"get Response Message : " + message);

               // ds.disconnect();
               // ds.close();


            }catch(SocketTimeoutException e){
            }catch(SocketException e){
            }catch(Exception e){
                Log.v(Debug_Tag,"sendData: Error\n" + e);
                e.printStackTrace();
            }
        }
        InetAddress getBroadcastAddress() throws IOException {
            WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            DhcpInfo dhcp =  wifi.getDhcpInfo();
            // handle null somehow

            int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
            byte[] quads = new byte[4];
            for (int k = 0; k < 4; k++)
                quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
            return InetAddress.getByAddress(quads);
        }

    } // end calss
    /*
    ////////////////////////////////////////////////////////////////////////////////
    // display message
    StringBuilder sb = new StringBuilder("");
    sb.append("send info : ");
    sb.append("Broadcast packet sent to: ");
    sb.append(getBroadcastAddress().getHostAddress());
    sb.append(" ");
    sb.append("send message : ");
    sb.append(s.toString());
    sb.append(" ");

    Log.v(Debug_Tag,sb.toString());

    Bundle data = new Bundle();
    data.putString("SendMessage",sb.toString());
    Message msg = new Message();
    msg.setData(data);
    Log.v(Debug_Tag,"mHandler.sendMessage(msg) : " + msg);
    mHandler.sendMessage(msg);
    */
    ////////////////////////////////////////////////////////////////////////////////
} // end mainactivity

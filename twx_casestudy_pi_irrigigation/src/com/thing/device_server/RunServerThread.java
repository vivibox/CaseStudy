package com.thing.device_server;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.thingworx.thing.DeviceThing;
import com.thingworx.thing.ThingProperty;
import com.thingworx.types.primitives.BooleanPrimitive;

public class RunServerThread implements Runnable {
	static Map<String, PrintWriter> allList ;
	private int portNumber ;
	private DeviceThing deviceThing ;
	// create class . add data in it 
//	ThingProperty temperature = SimpleThingClient.temperature ;
//	ThingProperty humidity = SimpleThingClient.humidity ;
//    ThingProperty setPoint = SimpleThingClient.setPoint ;
//    ThingService add = SimpleThingClient.add ;
	ThingProperty DeviceStatus ;

    
    public RunServerThread(int portNumber, DeviceThing deviceThing) {
    	this.portNumber = portNumber ;	
    	this.deviceThing = deviceThing ;
    	this.DeviceStatus = deviceThing.getDeviceStatus();
    }
    
	public void run() {
		try {
//			// open socket
		    Socket socket;
		    
			//connect
			final ServerSocket server = new ServerSocket(portNumber);
			allList = new HashMap<String, PrintWriter> ();
			System.out.println("Server:等待連線...");
			//次數計算
			int i = 1 ;
			int msg;
            
//------------ when socket accept ------------------------
			while(true) {				
				try {
					socket = server.accept();
					 // when socket accept
		              System.out.println("Server: Socket accept"); 
		             //	先讀取連接者名稱----------------------------------------------
		              BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		              String clientName = br.readLine();
		              System.out.println("!!!!!!!!!!!! clientName :: "+clientName );
		             

		              
		              
//		              因裝置以連線 => 設置DeviceStatus => true
		              DeviceStatus.setValue(new BooleanPrimitive(true));
		              System.out.println("RunServerThread:: DeviceStatus == true ");
// -------------------Thread with python
		              new Thread(new RecvMsg(socket,deviceThing)).start();
		              //new Thread(new SentMsg(socket,deviceThing)).start();
		              
		              allList.put(clientName, new PrintWriter(socket.getOutputStream()));
						System.out.println("Server:"+clientName+"將PW寫入LIST");
						i++ ;

						
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	             
	              
	       }

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} finally {
			//listener.close();
		}
		
	}
	
	// 讓TWX可以控制
	public void sendMsgToDevice(String name, String msg) {
		
		allList.get(name).println(msg);
		allList.get(name).flush();
	}

}	
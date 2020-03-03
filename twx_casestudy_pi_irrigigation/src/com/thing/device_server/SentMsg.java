package com.thing.device_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import com.thingworx.thing.DeviceThing;
import com.thingworx.thing.ThingProperty;
import com.thingworx.thing.ThingService;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;

import net.sf.json.JSONObject;

public class SentMsg implements Runnable{
private Socket socket ;
BufferedReader br;
PrintWriter pw;
int i = 1 ;

protected ThingProperty temperature ;
protected ThingProperty humidity ;
protected ThingProperty setPoint;
protected ThingService add;

protected ThingProperty powerLevel;
protected ThingProperty deviceStatus ;
protected ThingProperty rainfall  ;
protected ThingProperty switchDevicePowerOn ;
protected ThingProperty switchAutoShutdownOn;


public SentMsg(Socket socket,DeviceThing deviceThing) {
		this.socket = socket ;
		this.temperature = deviceThing.getTemperature();
		this.humidity = deviceThing.getHumidity();
		this.setPoint = deviceThing.getSetPoint();
		this.add = deviceThing.getAdd();
		
		this.switchDevicePowerOn = deviceThing.getSwitchDevicePowerOn();
		this.powerLevel = deviceThing.getPowerLevel();
		this.deviceStatus = deviceThing.getDeviceStatus();
		this.rainfall = deviceThing.getRainfall();
		this.switchAutoShutdownOn = deviceThing.getSwitchAutoShutdownOn();
		
		try {
//			寫出~~~~~~~~~
			pw = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("handled by thread");
	}
	@Override
	public void run() {
		System.out.println("Connected " +  socket);
		String msg;
		JSONObject j;
		while( true ) {
			// 如使用C語言連結，null會讀取不到 可能不會執行迴圈
			
			//-----------send data to device------------------------------  
			  String sendMsg = "{'Property':{'setPoint ':"+setPoint.getValue().getValue()+",'powerLevel ':"+ powerLevel.getValue().getValue() +",'switchAutoShutdownOn':"+switchAutoShutdownOn.getValue().getValue()+"},'Service':{'add':"+add.getReturnDoubleValue()+"}}";
			  
			  pw.println(sendMsg);
			  pw.flush();
			  i++ ;
			
		} 
		
	}

}

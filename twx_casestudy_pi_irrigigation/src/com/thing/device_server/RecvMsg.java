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

public class RecvMsg implements Runnable{
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
protected ThingProperty waterPressure;

public RecvMsg(Socket socket,DeviceThing deviceThing) {
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
		this.waterPressure = deviceThing.getWaterPressure();
		System.out.println("handled by thread");
	}
	@Override
	public void run() {
		char[] buffer = new char[1024];

		System.out.println("Connected " +  socket);
		String msg;
		JSONObject j;
		try {
			System.out.println("RECV:: Get in put stream");
	        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	        System.out.println("RECV:: Get in put stream in ::1");
	        
	        while(true) {
	          //receive data	          
	        	System.out.println("RECV:: Get in put stream in ::2");
	          if( br !=null && br.read(buffer) != -1) {
	        	msg = new String(buffer);
	  			System.out.println("Server: read from client : "+ msg);
//	  			get JSON value
	  			j = new JSONObject(msg);

	  			// get temperature
	        	  int temp = Integer.parseInt(j.getJSONObject("weather").get("Temp").toString());
	        	  temperature.setValue(new NumberPrimitive(temp));
	  			// get humidity
	        	int hum = Integer.parseInt(j.getJSONObject("weather").get("Humidity").toString());
	        	humidity.setValue(new NumberPrimitive(hum));
	        	
	        	//controller
      		  int dataChange = Integer.parseInt(j.getJSONObject("controller").get("DataChange").toString());
	        	  if(dataChange == 0) {
	        		//get Property => waterPressure
		        	  int wPressure = Integer.parseInt(j.getJSONObject("controller").get("waterPressure").toString());
		        	  waterPressure.setValue(new NumberPrimitive(wPressure));
	        	  }else if(dataChange == 1) {
		        	//get Property => switchDevicePowerOnOff
		        	  Boolean powerOnOff = Boolean.parseBoolean(j.getJSONObject("controller").get("switchDevicePowerOn").toString());
		        	  System.out.println("powerOnOff: "+powerOnOff);
		        	  switchDevicePowerOn.setValue(new BooleanPrimitive(powerOnOff));
		          }else if(dataChange == 2) {
		        	//get Property => PowerLevel
	        		  int power = Integer.parseInt(j.getJSONObject("controller").get("powerLevel").toString());
		        	  powerLevel.setValue(new NumberPrimitive(power));
		          }else if(dataChange == 3) {
		        	//get Property => switchDevicePowerOnOff
		        	  Boolean switchAutoShutdownOn = Boolean.parseBoolean(j.getJSONObject("controller").get("SwitchAutoShutdownOn").toString());
		        	  System.out.println("switchAutoShutdownOn: "+switchAutoShutdownOn);
		        	  switchDevicePowerOn.setValue(new BooleanPrimitive(switchAutoShutdownOn));
		          }else {
		        	  System.out.println("Have some unknow msg");
	        	  }
	  
	        	
	          
	          }else {
	        	  System.out.println("is not working");
	          }
	        }

		}
		catch (Exception e) {
			e.printStackTrace();
		}			


		
	}

}

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

public class ReceiveDevice implements Runnable{
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


public ReceiveDevice(Socket socket,DeviceThing deviceThing) {
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
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
		try {
			//Scanner in = new Scanner(socket.getInputStream());
			// BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			// String message = reader.readLine();
		
			while( true ) {
				// 如使用C語言連結，null會讀取不到 可能不會執行迴圈
				
				try {
					
					if(br != null) {
						msg=br.readLine() ;
						System.out.println("Server: read from client : "+ msg + " " + i);
						//set deviceStatus => true
						deviceStatus.setValue(new BooleanPrimitive(true));
						switchDevicePowerOn.setValue(new BooleanPrimitive(true));
		//-----------receive data from device------------------------------  
		        	 
						j = new JSONObject(msg);
			        	  System.out.println("2:j  :"+j);
			        	  Object property = j.getJSONObject("Property");  
			        	  // get Property => temperature
			        	  int temp = Integer.parseInt(j.getJSONObject("Property").get("temperature").toString());
			        	  temperature.setValue(new NumberPrimitive(temp));
				  
			        	  // get Property => humidity
			        	  int hum = Integer.parseInt(j.getJSONObject("Property").get("humidity").toString());
			        	  humidity.setValue(new NumberPrimitive(hum));
			        	  
			        	  //get Property => switchDevicePowerOnOff
			        	  Boolean powerOnOff = Boolean.parseBoolean(j.getJSONObject("Property").get("switchDevicePowerOn").toString());
			        	  System.out.println("powerOnOff: "+powerOnOff);
			        	  switchDevicePowerOn.setValue(new BooleanPrimitive(powerOnOff));
			
			        	  //get Property => PowerLevel
			        	  int power = Integer.parseInt(j.getJSONObject("Property").get("powerLevel").toString());
			        	  powerLevel.setValue(new NumberPrimitive(power));
			        	  
			        	//get Property => Rainfall
			        	  int rain = Integer.parseInt(j.getJSONObject("Property").get("rainfall").toString());
			        	  if(switchAutoShutdownOn.getValue().getValue().equals(true)) {
			        		  if(rain == 0) {
			        			  switchDevicePowerOn.setValue(new BooleanPrimitive(true));
			        		  }else {
			        			  switchDevicePowerOn.setValue(new BooleanPrimitive(false));
			        		  }
		        	  }
		        	  
		        	  
		        	  System.out.println("3:Server: read from client : property "+ property + " " + i);
		        	  System.out.println("3:Server: read from client : temperature "+ temp + " " + i);
		        	  System.out.println("3:Server: read from client : humidity "+ hum + " " + i);
		//        	  System.out.println("3:Server: read from client : powerOnOff "+ powerOnOff + " " + i);
		        	  System.out.println("3:Server: read from client : powerLevel "+ power + " " + i);
					}
				
        	//-----------send data to device------------------------------  
        	  String sendMsg = "{'Property':{'setPoint ':"+setPoint.getValue().getValue()+",'powerLevel ':"+ powerLevel.getValue().getValue() +",'switchAutoShutdownOn':"+switchAutoShutdownOn.getValue().getValue()+"},'Service':{'add':"+add.getReturnDoubleValue()+"}}";
        	  
        	  pw.println(sendMsg);
	          pw.flush();
	          i++ ;
				}catch(SocketException a) {
				   //set DeviceStatus => false
				  deviceStatus.setValue(new BooleanPrimitive(false));
				  switchDevicePowerOn.setValue(new BooleanPrimitive(false));
          		  System.out.println("A::Device connect"+deviceStatus.getValue().getValue()+" 連線中斷，請重新連線 !!!!");
          		  break;
//          		  throw a;
          	  }
				
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Error:" + socket);
			e.printStackTrace();
		} 
		
	}

}

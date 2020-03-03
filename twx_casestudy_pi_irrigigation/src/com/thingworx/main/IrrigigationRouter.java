package com.thingworx.main;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thing.device_server.RunServerThread;
import com.thingworx.communications.client.ClientConfigurator;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.sdk.simplething.RouterThing;
import com.thingworx.sdk.simplething.TwxThing;
import com.thingworx.thing.DeviceThing;
import com.thingworx.thing.Router;
import com.thingworx.thing.ThingProperty;
import com.thingworx.thing.ThingService;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;

public class IrrigigationRouter extends ConnectedThingClient {

    private static final Logger LOG = LoggerFactory.getLogger(IrrigigationRouter.class);
    private static Router irrigigation = new Router("irrigigation","3J57+6H ¥x¥_");
    //----------Device 1 ---------------------
    private static String ThingName = "Irr_device_1";
    private static int devicePortNumber = 2345 ;
    public static DeviceThing SimpleThing1 = new DeviceThing(ThingName,"Taoyuan");
    //----------Device 2 ---------------------
    private static String ThingName2 = "Irr_device_2";
    private static int devicePortNumber2 = 1234 ;
    public static DeviceThing SimpleThing2 = new DeviceThing(ThingName2,"Taipei");

    public IrrigigationRouter(ClientConfigurator config) throws Exception {
        super(config);
    }

    public static void main(String[] args) {
    	
    	//-----Setting Properties & services thing --------------------
    	//-----Server 1 Receive&Send Data to Client PORT2345------------   	
    	SimpleThing1.setTemperature(new ThingProperty("Temperature",new NumberPrimitive(0)));
    	SimpleThing1.setHumidity(new ThingProperty("Humidity",new NumberPrimitive(0)));
    	SimpleThing1.setSetPoint(new ThingProperty("SetPoint",new NumberPrimitive(70)));
    	SimpleThing1.setAdd(new ThingService("Add","result"));
    	SimpleThing1.setWaterPressure(new ThingProperty("WaterPressure",new NumberPrimitive(0)));
    	SimpleThing1.setPowerLevel(new ThingProperty("PowerLevel",new NumberPrimitive(3)));
    	SimpleThing1.setDeviceStatus(new ThingProperty("DeviceStatus",new BooleanPrimitive(false)) );
    	SimpleThing1.setSwitchAutoShutdownOn(new ThingProperty("SwitchAutoShutdownOn",new BooleanPrimitive(true)) );
    	SimpleThing1.setSwitchDevicePowerOn(new ThingProperty("SwitchDevicePowerOn",new BooleanPrimitive(false)) );
    	SimpleThing1.setRainfall(new ThingProperty("Rainfall",new NumberPrimitive(0)));
    	
    	// connect to device
    	RunServerThread sReceive = new RunServerThread(devicePortNumber,SimpleThing1);
  		Thread tReceive = new Thread(sReceive);
  		tReceive.start();
  		System.out.print("tReceive");
	
  		//-----Server 2 Receive&Send Data to Client PORT1234---------------
//    	SimpleThing2.setTemperature(new ThingProperty("temperature"));
//    	SimpleThing2.setHumidity(new ThingProperty("humidity"));
//    	SimpleThing2.setSetPoint(new ThingProperty("setPoint",new NumberPrimitive(1000)));
//    	SimpleThing2.setAdd(new ThingService("Add","result"));
//    	
//        RunServerThread sReceive2 = new RunServerThread(devicePortNumber2,SimpleThing2);
//  		Thread tReceive2 = new Thread(sReceive2);
//  		tReceive2.start();
//  		System.out.print("tReceive2");
    	
    	
        ClientConfigurator config = new ClientConfigurator();

        // Set the URI of the server that we are going to connect to
        config.setUri("ws://Paste Your Uri");

        // Set the ApplicationKey. This will allow the client to authenticate with the server.
        // It will also dictate what the client is authorized to do once connected.
        config.setAppKey("Change to your Appkey");

        // This will allow us to test against a server using a self-signed certificate.
        // This should be removed for production systems.
        config.ignoreSSLErrors(true); // All self signed certs

        try {

            // Create our client.
        	IrrigigationRouter client = new IrrigigationRouter(config);

            // Start the client. The client will connect to the server and authenticate
            // using the ApplicationKey specified above.
            client.start();

            // Wait for the client to connect.
            if (client.waitForConnection(30000)) {

                LOG.info("The client is now connected.");

                //
                // Create a VirtualThing and bind it to the client
                ///////////////////////////////////////////////////////////////

                // Create a new VirtualThing. The name parameter should correspond with the
                // name of a RemoteThing on the Platform.
                
// => CHANGE ----------------new SimpleThing ------START-----------------------------------------------------
                TwxThing thing = new TwxThing(ThingName, "A basic virtual thing", client , SimpleThing1, sReceive);
//                TwxThing thing2 = new TwxThing(ThingName2, "A basic virtual thing2", client, SimpleThing2);
                List<TwxThing> deviceList = new ArrayList<TwxThing>();
                deviceList.add(thing);
//                deviceList.add(thing2);
                RouterThing router = new RouterThing("Router_1", "router", client, deviceList) ;
//---------------------------new SimpleThing ------END-----------------------------------------------------
                // Bind the VirtualThing to the client. This will tell the Platform that
                // the RemoteThing 'Simple1' is now connected and that it is ready to
                // receive requests.
                client.bindThing(thing);
//                client.bindThing(thing2);
            	client.bindThing(router);
                // This will prevent the main thread from exiting. It will be up to another thread
                // of execution to call client.shutdown(), allowing this main thread to exit.
                while (!client.isShutdown()) {

                    Thread.sleep(10000);

                    // Every 15 seconds we tell the thing to process a scan request. This is
                    // an opportunity for the thing to query a data source, update property
                    // values, and push new property values to the server.
                    //
                    // This loop demonstrates how to iterate over multiple VirtualThings
                    // that have bound to a client. In this simple example the things
                    // collection only contains one VirtualThing.
                    for (VirtualThing vt : client.getThings().values()) {
                        vt.processScanRequest();
                    }
                }

            } else {
                // Log this as a warning. In production the application could continue
                // to execute, and the client would attempt to reconnect periodically.
                LOG.warn("Client did not connect within 30 seconds. Exiting");
            }

        } catch (Exception e) {
            LOG.error("An exception occurred during execution.", e);
        }
        
        LOG.info("SimpleThingClient is done. Exiting");        	
    }

}

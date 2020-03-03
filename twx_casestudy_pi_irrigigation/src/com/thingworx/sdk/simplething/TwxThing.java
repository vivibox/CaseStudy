package com.thingworx.sdk.simplething;



import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thing.device_server.RunServerThread;
import com.thingworx.communications.client.ConnectedThingClient;
import com.thingworx.communications.client.things.VirtualThing;
import com.thingworx.main.IrrigigationRouter;
import com.thingworx.metadata.PropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinition;
import com.thingworx.metadata.annotations.ThingworxPropertyDefinitions;
import com.thingworx.metadata.annotations.ThingworxServiceDefinition;
import com.thingworx.metadata.annotations.ThingworxServiceParameter;
import com.thingworx.metadata.annotations.ThingworxServiceResult;
import com.thingworx.thing.DeviceThing;
import com.thingworx.thing.ThingProperty;
import com.thingworx.thing.ThingService;
import com.thingworx.types.primitives.BooleanPrimitive;
import com.thingworx.types.primitives.IPrimitiveType;
import com.thingworx.types.primitives.IntegerPrimitive;
import com.thingworx.types.primitives.NumberPrimitive;
import com.thingworx.types.primitives.StringPrimitive;

@ThingworxPropertyDefinitions(properties = {
		
// -------------------------NUMBER----------------------------------------------------------
        // This property is setup for collecting time series data. Each value
        // that is collected will be pushed to the platfrom from within the
        // processScanRequest() method.
        @ThingworxPropertyDefinition(name = "Temperature", description = "The device temperature",
                baseType = "NUMBER",
                aspects = { "dataChangeType:NEVER", "dataChangeThreshold:0", "cacheTime:0",
                        "isPersistent:FALSE", "isReadOnly:FALSE", "pushType:ALWAYS",
                        "isFolded:FALSE", "defaultValue:0" }),

        // This property is also pushed to the platform, but only when the value
        // of the property has changed.
        @ThingworxPropertyDefinition(name = "Humidity", description = "The device humidity",
                baseType = "NUMBER",
                aspects = { "dataChangeType:VALUE", "dataChangeThreshold:0", "cacheTime:0",
                        "isPersistent:FALSE", "isReadOnly:FALSE", "pushType:VALUE",
                        "defaultValue:0" }),

        // This property is never pushed to the platform. The platform will always
        // request the values current value from the application.
        @ThingworxPropertyDefinition(name = "SetPoint", description = "The desired temperature",
                baseType = "NUMBER",
                aspects = { "dataChangeType:NEVER", "dataChangeThreshold:0", "cacheTime:-1",
                        "isPersistent:TRUE", "isReadOnly:FALSE", "pushType:NEVER",
                        "defaultValue:70" }),

        @ThingworxPropertyDefinition(name = "PowerLevel", description = "Irrigation Power Level",
        baseType = "NUMBER",
        aspects = { "dataChangeType:VALUE", "dataChangeThreshold:0", "cacheTime:0",
                "isPersistent:FALSE", "isReadOnly:FALSE", "pushType:ALWAYS",
                "defaultValue:3" }),
        
        @ThingworxPropertyDefinition(name = "WaterPressure", description = "Pump Water Pressure",
        baseType = "NUMBER",
        aspects = { "dataChangeType:NEVER", "dataChangeThreshold:0", "cacheTime:0",
                "isPersistent:FALSE", "isReadOnly:FALSE", "pushType:ALWAYS",
                "isFolded:FALSE", "defaultValue:0" }),

     
        // ----------------BOOLEAN--------------------------------
        @ThingworxPropertyDefinition(name = "SwitchDevicePowerOn", description = "Turn on / turn off device",
        baseType = "BOOLEAN",
        aspects = { "dataChangeType:VALUE", "dataChangeThreshold:0", "cacheTime:0",
                "isPersistent:TRUE", "isReadOnly:FALSE", "pushType:VALUE",
                "defaultValue:FALSE" }),  // (Device	O | ThingWorx	O)
        
        @ThingworxPropertyDefinition(name = "SwitchAutoShutdownOn", description = "Turn on / turn off the faction AutoShutdown",
        baseType = "BOOLEAN",
        aspects = { "dataChangeType:NEVER", "dataChangeThreshold:0", "cacheTime:-1",
                "isPersistent:TRUE", "isReadOnly:FALSE", "pushType:NEVER",
                "defaultValue:TRUE" }), // (Device	O | ThingWorx	O)
        
        @ThingworxPropertyDefinition(name = "DeviceStatus", description = "Check device is connected or not",
        baseType = "BOOLEAN",
        aspects = { "dataChangeType:VALUE", "dataChangeThreshold:0", "cacheTime:0",
                "isPersistent:TRUE", "isReadOnly:FALSE", "pushType:ALWAYS",
                "defaultValue:FALSE" }),
        // ----------------TEXT--------------------------------
        @ThingworxPropertyDefinition(name = "RouterName", description = "The device is in which router",
        baseType = "TEXT",
        aspects = { "dataChangeType:NEVER", "dataChangeThreshold:0", "cacheTime:-1",
                "isPersistent:TRUE", "isReadOnly:FALSE", "pushType:VALUE",
                "defaultValue:Router_1" })		})

		
/**
 * A very basic VirtualThing with two properties and a service implementation. It also implements
 * processScanRequest to handle periodic actions.
 */
public class TwxThing extends VirtualThing {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(TwxThing.class);
	//---------<add thing change here>------------------------------------------------
//	protected DeviceThing SimpleThing ; 
	//---------<add thing change here>------------------------------------------------

    protected  ThingProperty temperature;
    protected  ThingProperty humidity ;
	protected  ThingProperty setPoint ;
	protected  ThingService add ;
	
	protected ThingProperty switchDevicePowerOn ;	
	protected ThingProperty deviceStatus ;	
	protected ThingProperty waterPressure ; 
	protected ThingProperty powerLevel ;
	// raining or not => turn off/on  
	protected ThingProperty switchAutoShutdownOn ; 

	private RunServerThread server;
    /**
     * A custom constructor. We implement this so we can call initializeFromAnnotations, which
     * processes all of the VirtualThing's annotations and applies them to the object.
     * 
     * @param name The name of the thing.
     * @param description A description of the thing.
     * @param client The client that this thing is associated with.
     */
	public TwxThing(String name, String description, ConnectedThingClient client)
            throws Exception {

        super(name, description, client);
        this.initializeFromAnnotations();

        try {
            this.setPropertyValue("SetPoint", new IntegerPrimitive(70));
            this.setPropertyValue("SwitchAutoShutdownOn", new BooleanPrimitive(true));
            this.setPropertyValue("switchDevicePowerOn", new BooleanPrimitive(false));
           
        } catch (Exception e) {
            LOG.warn("Could not ser default value for SetPoint/SwitchAutoShutdownOn");
        }
    }
	
	public TwxThing(String name, String description, ConnectedThingClient client, DeviceThing deviceThing)
            throws Exception {

        super(name, description, client);
        this.initializeFromAnnotations();
        
        //get properties
        this.temperature = deviceThing.getTemperature();
        this.humidity = deviceThing.getHumidity();
    	this.setPoint = deviceThing.getSetPoint();
    	this.add = deviceThing.getAdd();
    	
    	this.switchDevicePowerOn = deviceThing.getSwitchDevicePowerOn();	
    	this.deviceStatus = deviceThing.getDeviceStatus();	
    	this.waterPressure = deviceThing.getWaterPressure(); 
    	this.powerLevel = deviceThing.getPowerLevel();
    	// raining or not => turn off/on  
    	this.switchAutoShutdownOn = deviceThing.getSwitchAutoShutdownOn(); 

        
        
        
        try {
            this.setPropertyValue("SetPoint", new IntegerPrimitive(70));
            this.setPropertyValue("SwitchAutoShutdownOn", new BooleanPrimitive(true));
            this.setPropertyValue("switchDevicePowerOn", new BooleanPrimitive(false));
           
        } catch (Exception e) {
            LOG.warn("Could not ser default value for SetPoint/SwitchAutoShutdownOn");
        }
    }

    public TwxThing(String name, String description, ConnectedThingClient client, DeviceThing deviceThing,
			RunServerThread sReceive) throws Exception {
        super(name, description, client);
    	server = sReceive;
    	
        this.initializeFromAnnotations();
        
        //get properties
        this.temperature = deviceThing.getTemperature();
        this.humidity = deviceThing.getHumidity();
    	this.setPoint = deviceThing.getSetPoint();
    	this.add = deviceThing.getAdd();
    	
    	this.switchDevicePowerOn = deviceThing.getSwitchDevicePowerOn();	// (Device	O | ThingWorx	O)
    	this.deviceStatus = deviceThing.getDeviceStatus();	// (Device	X | ThingWorx	X  | 當有異常中斷時觸發)
    	this.waterPressure = deviceThing.getWaterPressure(); // (Device	O | ThingWorx	X)
    	this.powerLevel = deviceThing.getPowerLevel();// (Device	O | ThingWorx	O)
    	// raining or not => turn off/on  
    	this.switchAutoShutdownOn = deviceThing.getSwitchAutoShutdownOn(); // (Device	O | ThingWorx	O)

        try {
            this.setPropertyValue("SetPoint", new IntegerPrimitive(70));
            this.setPropertyValue("SwitchAutoShutdownOn", new BooleanPrimitive(true));
            this.setPropertyValue("switchDevicePowerOn", new BooleanPrimitive(false));
           
        } catch (Exception e) {
            LOG.warn("Could not ser default value for SetPoint/SwitchAutoShutdownOn");
        }
	}

	/**
     * This method provides a common interface amongst VirtualThings for processing periodic
     * requests. It is an opportunity to access data sources, update property values, push new
     * values to the server, and take other actions.
     */
    @Override
    public void processScanRequest() {

        // We'll use this to generate a random temperature and humidity value.
        // On an actual system you would access a sensor or some other data source.

        try {

            // Here we set the thing's internal property values to the new values
            // that we accessed above. This does not update the server. It simply
            // sets the new property value in memory.
            this.setPropertyValue( "Temperature", temperature.getValue());
            this.setPropertyValue( "Humidity", humidity.getValue());
        	this.setPropertyValue( "WaterPressure", waterPressure.getValue()) ; 
        	this.setPropertyValue( "PowerLevel", powerLevel.getValue()) ;
        	this.setPropertyValue( "DeviceStatus", deviceStatus.getValue()) ;
        	this.setPropertyValue( "SwitchDevicePowerOn", switchDevicePowerOn.getValue()) ;
        	this.setPropertyValue("RouterName", new StringPrimitive("Router_1"));

            // This call evaluates all properties and determines if they should be pushed
            // to the server, based on their pushType aspect. A pushType of ALWAYS means the
            // property will always be sent to the server when this method is called. A
            // setting of VALUE means it will be pushed if has changed since the last
            // push. A setting of NEVER means it will never be pushed.
            //
            // Our Temperature property is set to ALWAYS, so its value will be pushed
            // every time processScanRequest is called. This allows the platform to get
            // periodic updates and store the time series data. Humidity is set to
            // VALUE, so it will only be pushed if it changed.
            this.updateSubscribedProperties(10000);

        } catch (Exception e) {
            // This will occur if we provide an unknown property name. We'll ignore
            // the exception in this case and just log it.
            LOG.error("Exception occurred while updating properties.", e);
        }
    }

    /**
     * This is where we handle property writes from the server. The only property we want to update
     * is the SetPoint. Temperature and Humidity write requests should be rejected, since their
     * values are controlled from within this application.
     * 
     * @see VirtualThing#processPropertyWrite(PropertyDefinition, IPrimitiveType)
     */
    @Override
    public void processPropertyWrite(PropertyDefinition property,
            @SuppressWarnings("rawtypes") IPrimitiveType value) throws Exception {

    	
        // Find out which property is being updated
        String propName = property.getName();
      
//        setPoint.setValue(value);
//        System.out.println("Server: 傳送SetPoint值:"+ value.getValue());

//        當ThingWorx變更時，此處傳值
        if ("SetPoint".equals(propName)) {
        	setPoint.setValue(value);
        	System.out.println("Server: 傳送SetPoint值:"+ value.getValue());
        	server.sendMsgToDevice("pyDevice1", "SetPoint#"+value.getValue());
        }
        else if ("SwitchDevicePowerOn".equals(propName)) {
        	if( deviceStatus.getValue().getValue().equals(true) ){
        		switchDevicePowerOn.setValue(value);
        		System.out.println("Server: 傳送SwitchDevicePowerOn值:"+ value.getValue());
        		if( (boolean) value.getValue()) {
//            		傳送訊息給Py
            		server.sendMsgToDevice("pyDevice1", "SwitchDevicePowerOn#"+ value.getValue());
            		System.out.println("Server::傳送給Device ----- SwitchDevicePowerOn值 ON---:"+ value.getValue());
        		}else {
        			server.sendMsgToDevice("pyDevice1", "setDevicePowerOff#"+ value.getValue());
        			System.out.println("Server::傳送給Device ----- SwitchDevicePowerOn值 OFF---:"+ value.getValue());
        		}

        	}
        	else {
            	throw new Exception("The device is not connect yet. " + propName + " can't work");
        	}
        }
        else if ("SwitchAutoShutdownOn".equals(propName)) {
        	switchAutoShutdownOn.setValue(value);
        	System.out.println("Server: 傳送switchAutoShutdownOn值:"+ value.getValue());
        	server.sendMsgToDevice("pyDevice1", "switchAutoShutdownOn#"+value.getValue());
        	System.out.println("Server::傳送給Device ----- switchAutoShutdownOn值 ON---:"+ value.getValue());
        }
        else {
        	throw new Exception("The property " + propName + " is read only on the simple device.");
        }
        
//        if (!"SetPoint".equals(propName)) {
//            throw new Exception("The property " + propName + " is read only on the simple device.");
//        }

        this.setPropertyValue(propName, value);
    }

    // The following annotation allows you to make a method available to the
    // ThingWorx Server for remote invocation. The annotation includes the
    // name of the server, the name and base types for its parameters, and
    // the base type of its result.
    @ThingworxServiceDefinition(name = "Add", description = "Add two numbers")
    @ThingworxServiceResult(name = "result", description = "The sum of the two parameters",
            baseType = "NUMBER")
    public Double Add(
            @ThingworxServiceParameter(name = "p1",
                    description = "The first addend of the operation",
                    baseType = "NUMBER") Double p1,
            @ThingworxServiceParameter(name = "p2",
                    description = "The second addend of the operation",
                    baseType = "NUMBER") Double p2)
            throws Exception {

        LOG.info("Adding the numbers {} and {}", p1, p2);
        add.setReturnDoubleValue(p1+p2);
        System.out.println("Router:: add return number = " + add.getReturnDoubleValue());
        return p1 + p2;
    }
}

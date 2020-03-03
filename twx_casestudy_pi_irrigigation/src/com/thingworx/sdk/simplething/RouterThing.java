package com.thingworx.sdk.simplething;



import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * A very basic VirtualThing with two properties and a service implementation. It also implements
 * processScanRequest to handle periodic actions.
 */
@ThingworxPropertyDefinitions(properties = {
		
		 // ----------------TEXT--------------------------------
	        @ThingworxPropertyDefinition(name = "RouterLocation", description = "The router location",
	        baseType = "TEXT",
	        aspects = { "dataChangeType:NEVER", "dataChangeThreshold:0", "cacheTime:-1",
	                "isPersistent:TRUE", "isReadOnly:FALSE", "pushType:VALUE",
	                "defaultValue:3J57+6H台北" })
})



public class RouterThing extends VirtualThing {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(RouterThing.class);
	//---------<add thing change here>------------------------------------------------
	protected DeviceThing SimpleThing = IrrigigationRouter.SimpleThing1 ; 
	//---------<add thing change here>------------------------------------------------

    protected  ThingProperty temperature = SimpleThing.getTemperature();
    protected  ThingProperty humidity = SimpleThing.getHumidity();
	protected  ThingProperty setPoint = SimpleThing.getSetPoint();
	protected  ThingService add = SimpleThing.getAdd();
	
	protected ThingProperty switchDevicePowerOn = SimpleThing.getSwitchDevicePowerOn();	// (Device	O | ThingWorx	O)
	protected ThingProperty deviceStatus = SimpleThing.getDeviceStatus();	// (Device	X | ThingWorx	X  | 當有異常中斷時觸發)
	protected ThingProperty waterPressure = SimpleThing.getWaterPressure(); // (Device	O | ThingWorx	X)
	protected ThingProperty powerLevel = SimpleThing.getPowerLevel();// (Device	O | ThingWorx	O)
	// raining or not => turn off/on  
	protected ThingProperty switchAutoShutdownOn = SimpleThing.getSwitchAutoShutdownOn(); // (Device	O | ThingWorx	O)

	private List<TwxThing> deviceList = new ArrayList<>();
    /**
     * A custom constructor. We implement this so we can call initializeFromAnnotations, which
     * processes all of the VirtualThing's annotations and applies them to the object.
     * 
     * @param name The name of the thing.
     * @param description A description of the thing.
     * @param client The client that this thing is associated with.
     */
	public RouterThing(String name, String description, ConnectedThingClient client, List<TwxThing> deviceList2)
            throws Exception {

        super(name, description, client);
        this.initializeFromAnnotations();
        deviceList = deviceList2;
        try {
            this.setPropertyValue("SetPoint", new IntegerPrimitive(70));
            this.setPropertyValue("SwitchAutoShutdownOn", new BooleanPrimitive(true));
           
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
        	this.setPropertyValue("RouterLocation", new StringPrimitive("3J57+6H台北"));

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
            this.updateSubscribedProperties(100000);

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
        }
        else if ("SwitchDevicePowerOn".equals(propName)) {
        	if( deviceStatus.getValue().getValue().equals(true) ){
        		switchDevicePowerOn.setValue(value);
        		System.out.println("Server: 傳送SwitchDevicePowerOn值:"+ value.getValue());
        	}
        	else {
            	throw new Exception("The device is not connect yet. " + propName + " can't work");
        	}
        }
        else if ("SwitchAutoShutdownOn".equals(propName)) {
        	switchAutoShutdownOn.setValue(value);
        	System.out.println("Server: 傳送switchAutoShutdownOn值:"+ value.getValue());
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
    
    @ThingworxServiceDefinition(name = "getDevices", description = "calculat number of  this device")
    @ThingworxServiceResult(name = "result", description = "The sum of the two parameters",
            baseType = "NUMBER")
    public Double getDevices()
            throws Exception {

//    	this.deviceList.get(0).switchDevicePowerOn = false;
    	return (double) this.deviceList.size();
    }
}

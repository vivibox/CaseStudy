package com.thingworx.thing;

import com.thingworx.types.primitives.NumberPrimitive;

public class DeviceThing{
	protected String thingName ;
	protected String location ; // (Device	X | ThingWorx	X)
	
	// weather => rainfall>0 > 
	protected ThingProperty temperature ;	// (Device	O | ThingWorx	X)
	protected ThingProperty humidity ;	// (Device	O | ThingWorx	X)
	protected ThingProperty setPoint ;	// (Device	O | ThingWorx	O)
	protected ThingService add ;	// (Device	X | ThingWorx	O)
	// device state
	protected ThingProperty switchDevicePowerOn ;	// (Device	O | ThingWorx	O)
	protected ThingProperty deviceStatus ;	// (Device	X | ThingWorx	X  | 當有異常中斷時觸發)
	protected ThingProperty waterPressure ; // (Device	O | ThingWorx	X)
	protected ThingProperty powerLevel ;// (Device	O | ThingWorx	O)
	// rainfall > 0 => turn off/on  
	protected ThingProperty switchAutoShutdownOn ; // (Device	O | ThingWorx	O)
	protected ThingProperty rainfall ;
	
	
	public DeviceThing(String thingName,String location) {
		this.thingName = thingName ;
		this.location = location ;
	}

	public String getThingName() {
		return thingName;
	}

	public void setThingName(String thingName) {
		this.thingName = thingName;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

//--------------set/get Thing property & service START --------------------------------


	public ThingProperty getTemperature() {
		return temperature ;
	}
	
	public void setTemperature(ThingProperty temperature) {
		this.temperature = temperature ;
	}

	public  ThingProperty getHumidity() {
		return humidity;
	}

	public void setHumidity(ThingProperty humidity) {
		this.humidity = humidity;
	}

	public ThingProperty getSetPoint() {
		return setPoint;
	}

	public void setSetPoint(ThingProperty setPoint) {
		this.setPoint = setPoint;
	}

	public ThingService getAdd() {
		return add;
	}

	public void setAdd(ThingService add) {
		this.add = add;
	}

	public ThingProperty getSwitchDevicePowerOn() {
		return switchDevicePowerOn;
	}

	public void setSwitchDevicePowerOn(ThingProperty switchDevicePowerOnOff) {
		this.switchDevicePowerOn = switchDevicePowerOnOff;
	}

	public ThingProperty getDeviceStatus() {
		return deviceStatus;
	}

	public void setDeviceStatus(ThingProperty deviceStatus) {
		this.deviceStatus = deviceStatus;
	}

	public ThingProperty getWaterPressure() {
		return waterPressure;
	}

	public void setWaterPressure(ThingProperty waterPressure) {
		this.waterPressure = waterPressure;
	}

	public ThingProperty getPowerLevel() {
		return powerLevel;
	}

	public void setPowerLevel(ThingProperty powerLevel) {
		this.powerLevel = powerLevel;
	}

	public ThingProperty getSwitchAutoShutdownOn() {
		return switchAutoShutdownOn;
	}

	public void setSwitchAutoShutdownOn(ThingProperty switchAutoShutdown) {
		this.switchAutoShutdownOn = switchAutoShutdown;
	}

	public ThingProperty getRainfall() {
		return rainfall;
	}

	public void setRainfall(ThingProperty rainfall) {
		this.rainfall = rainfall;
	}
	
	
	
//--------------set/get Thing property & service END ----------------------------------
	
	
}

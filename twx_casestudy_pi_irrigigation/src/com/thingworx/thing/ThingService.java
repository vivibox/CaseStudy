package com.thingworx.thing;

import com.thingworx.types.primitives.IPrimitiveType;

public class ThingService {
	protected String serviceName;
	protected String returnName ;
	protected double returnDoubleValue ;
	protected String returnStringValue ;
	
	public ThingService(String serviceName,String returnName) {
		this.serviceName = serviceName ;
		this.returnName = returnName ;
	}
	public ThingService(String serviceName,String returnName,double returnDoubleValue) {
		this.serviceName = serviceName ;
		this.returnName = returnName ;
		this.returnDoubleValue = returnDoubleValue ;
	}
	public ThingService(String serviceName,String returnName,String returnStringValue) {
		this.serviceName = serviceName ;
		this.returnName = returnName ;
		this.returnStringValue = returnStringValue ;
	}
	
	public String getServiceName() {
		return serviceName;
	}
	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}
	public String getReturnName() {
		return returnName;
	}
	public void setReturnName(String returnName) {
		this.returnName = returnName;
	}
	public double getReturnDoubleValue() {
		return returnDoubleValue;
	}
	public void setReturnDoubleValue(double returnDoubleValue) {
		this.returnDoubleValue = returnDoubleValue;
	}
	public String getReturnStringValue() {
		return returnStringValue;
	}
	public void setReturnStringValue(String returnStringValue) {
		this.returnStringValue = returnStringValue;
	}


	
	
}

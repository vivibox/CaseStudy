package com.thingworx.thing;

import com.thingworx.types.primitives.IPrimitiveType;

public class ThingProperty {
	protected String name;
	protected IPrimitiveType value ;

	
	public ThingProperty(String propertyName,IPrimitiveType value) {
		this.name = propertyName ;
		this.value = value ;
		
	}
	
	public ThingProperty(String propertyName) {
		this.name = propertyName ;
	}

	public String getPropertyName() {
		return name;
	}

	public void setPropertyName(String propertyName) {
		this.name = propertyName;
	}

	public IPrimitiveType getValue() {
		return value;
	}

	public void setValue(IPrimitiveType value) {
		this.value = value;
	}
}

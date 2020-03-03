package com.thingworx.error;

public class ConnectBreakException extends Exception {
	
	public ConnectBreakException() {
		super();
	}
	public ConnectBreakException(String msg) {
		System.out.println(msg);
	}
}

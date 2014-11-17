package com.hictech.htmlplus.servlet;



public interface PlusListener {
	
	public void plusStart(Object event);
	public void plusClose(Object event);
	
	public void monadeStart(Object event);
	public void monadeClose(Object event);
	
	public void hostStart(Object event);
	public void hostClose(Object event);
	
	public void clientStart(Object event);
	public void clientClose(Object event);
	
}

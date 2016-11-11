package com.component.benjamin.utils.testframe;

import java.util.Map;
import java.util.Set;

public class StatisticThread implements Runnable{

	Object object;
	
	public void run() {
		
		object.toString();
		
	}
	
	public void setObject(Object object) {
		this.object = object;
	}
	
}

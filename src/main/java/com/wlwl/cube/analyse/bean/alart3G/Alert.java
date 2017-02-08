package com.wlwl.cube.analyse.bean.alart3G;

import java.util.List;

import com.wlwl.cube.analyse.bean.GMSEvent;
import com.wlwl.cube.analyse.bean.ObjectModelOfKafka;

public class Alert extends ObjectModelOfKafka {
	public  List<GMSEvent> events;

	public double latitude=0;

	public double longitude=0;
	
	public int getEventCount() {
		return eventCount;
	}

	public void setEventCount(int eventCount) {
		this.eventCount = eventCount;
	}

	public  int eventCount = 0;

	public List<GMSEvent> getEvents() {
		return events;
	}

	public void setEvents(List<GMSEvent> events) {
		this.events = events;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public  void parsePrivateOctets() throws Exception{}
}

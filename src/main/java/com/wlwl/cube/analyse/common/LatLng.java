package com.wlwl.cube.analyse.common;

public class LatLng {
	private String lat;
	private String lng;
	
	public LatLng(String lng,String lat)
	{
		this.lat=lat;
		this.lng=lng;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getLng() {
		return lng;
	}
	public void setLng(String lng) {
		this.lng = lng;
	}
	
	public String toString()
	{
		return lng+","+lat;
	}


}

package cn.wlwl.cube.ananlyse.state.alarm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.wlwl.cube.analyse.common.alarm.LatLng;
import cn.wlwl.cube.analyse.common.alarm.PossionInfo;

public class AMapConvertService {
	
	
	private static String targetURL = "http://restapi.amap.com/v3/assistant/coordinate/convert?key=f25e2512162cf4114dba38fec6faab61&locations={0}&coordsys=gps";
	public static LatLng getConvert(LatLng latlng)
	{
		LatLng latlngs=null;
		String temp =targetURL.replace("{0}", latlng.getLng()+","+latlng.getLat());
		
		try {

            URL restServiceURL = new URL(temp);

            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");

            if (httpConnection.getResponseCode() != 200) {
                   throw new RuntimeException("HTTP GET Request Failed with Error code : "
                                 + httpConnection.getResponseCode());
            }

            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                   (httpConnection.getInputStream())));

            String output;
            //System.out.println("Output from Server:  \n");
            StringBuilder sb=new StringBuilder();
            while ((output = responseBuffer.readLine()) != null) {
                   sb.append(output);
            }
            
            PossionInfo   ps=    JsonUtils.deserialize(sb.toString(), PossionInfo.class);
            
        	String[] possion=ps.toString().split(",");
        	latlngs=new LatLng(possion[0],possion[1]);
            httpConnection.disconnect();

       } catch (MalformedURLException e) {

            e.printStackTrace();

       } catch (IOException e) {

            e.printStackTrace();

       }
		return latlngs;
		
	}

  //  private static final String targetURL = "http://restapi.amap.com/v3/assistant/coordinate/convert?key=f25e2512162cf4114dba38fec6faab61&locations={0}&coordsys=gps";
    public static void main(String[] args) {
    	LatLng latlng=getConvert(new LatLng(String.valueOf(0),String.valueOf(0)));
    	System.out.println(latlng);
      }
}


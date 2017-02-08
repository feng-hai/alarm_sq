package com.wlwl.cube.analyse.filter;

import org.apache.storm.trident.operation.BaseFilter;
import org.apache.storm.trident.tuple.TridentTuple;

import com.wlwl.cube.analyse.bean.ObjectModelOfKafka;

public class VehicleFilter extends BaseFilter{
	


	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public void PerActorTweetsFilter() {

	  }
	  @Override
	  public boolean isKeep(TridentTuple tuple) {
		  
	 ObjectModelOfKafka omok = (ObjectModelOfKafka) tuple.getValueByField("vehicle");
	 String temp = omok.getRAW_OCTETS().substring(10, 14);
	// System.out.println(temp);
	 return  temp.equals("E702")||temp.equals("8B03");//tuple.getString(0).equals(actor);
	    
	  }

}

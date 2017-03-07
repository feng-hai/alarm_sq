package com.wlwl.cube.analyse.filter;

import org.apache.storm.trident.operation.BaseFilter;
import org.apache.storm.trident.tuple.TridentTuple;

import com.wlwl.cube.analyse.bean.ObjectModelOfKafka;

public class VehicleFilter extends BaseFilter {

	/**
	* 
	*/
	private static final long serialVersionUID = 1L;

	public void PerActorTweetsFilter() {

	}

	@Override
	public boolean isKeep(TridentTuple tuple) {

		ObjectModelOfKafka omok = (ObjectModelOfKafka) tuple.getValueByField("vehicle");
		String temp = omok.getRAW_OCTETS().replaceAll("7D01", "7E").replaceAll("7D02", "7D").substring(10, 14);
		String temp2 = omok.getRAW_OCTETS().replaceAll("7D01", "7E").replaceAll("7D02", "7D").substring(1, 4);
		// System.out.println(temp);
		return temp.equals("E702") || temp.equals("8B03") || temp2.equals("E702") || temp2.equals("8B03");// tuple.getString(0).equals(actor);

	}

}

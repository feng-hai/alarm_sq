/**  
* @Title: SaveValueToRedisFunction.java
* @Package com.wlwl.cube.ananlyse.functions
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年9月27日 下午7:04:17
* @version V1.0.0  
*/
package com.wlwl.cube.ananlyse.functionsForAlarm;

import java.util.Map;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;

import com.wlwl.cube.analyse.bean.ObjectModelOfKafka;

import com.wlwl.cube.analyse.bean.VehicleStatisticBean;
import com.wlwl.cube.analyse.bean.alart3G.Alert;
import com.wlwl.cube.analyse.bean.alart3G.OX02E7Alert;
import com.wlwl.cube.analyse.bean.alart3G.OX038BAlert;

import com.wlwl.cuble.analyse.storager.StoragerSingleton;

/**
 * @ClassName: SaveValueToRedisFunction
 * @Description: TODO
 * @author fenghai
 * @date 2017年2月6日 下午7:04:17
 *
 */
public class AnalysisAlarmDataFunction extends BaseFunction {

	private static final long serialVersionUID = 4608482736186526306L;
	private static final String preKey = "BIG_ANALYSIS:";
	@Override
	public void prepare(Map conf, TridentOperationContext context) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.storm.trident.operation.Function#execute(org.apache.storm.
	 * trident.tuple.TridentTuple,
	 * org.apache.storm.trident.operation.TridentCollector)
	 */
	public void execute(TridentTuple tuple, TridentCollector collector) {

		try {
			ObjectModelOfKafka vehicleInfo = (ObjectModelOfKafka) tuple.getValueByField("vehicle");
			String temp = vehicleInfo.getRAW_OCTETS().substring(10, 14);
			Alert alarm = null;
			if (temp.equals("E702")) {
				alarm = new OX02E7Alert(vehicleInfo);
			
			} else if (temp.equals("8B03")) {
				alarm = new OX038BAlert(vehicleInfo);
			}
			
             alarm.setDEVICE_ID(vehicleInfo.getDEVICE_ID());
             alarm.setUnid(vehicleInfo.getUnid());
             alarm.setNode_unid(vehicleInfo.getNode_unid());
			collector.emit(new Values(alarm));

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

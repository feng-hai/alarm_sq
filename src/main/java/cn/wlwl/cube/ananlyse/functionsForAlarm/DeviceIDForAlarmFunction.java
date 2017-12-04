/**  
* @Title: DeviceIDFunction.java
* @Package com.wlwl.cube.ananlyse.functions
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年9月26日 上午11:09:56
* @version V1.0.0  
*/
package cn.wlwl.cube.ananlyse.functionsForAlarm;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;

import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;

import cn.wlwl.cube.analyse.bean.alarm.ObjectModelOfKafka;
import cn.wlwl.cube.analyse.bean.alarm.Pair;


/**
 * @ClassName: DeviceIDFunction
 * @Description: TODO找到终端id，并传递出去
 * @author fenghai
 * @date 2016年9月26日 上午11:09:56
 *
 */
public class DeviceIDForAlarmFunction extends BaseFunction {

	private static final long serialVersionUID = -3430938120228163893L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.storm.trident.operation.Function#execute(org.apache.storm.
	 * trident.tuple.TridentTuple,
	 * org.apache.storm.trident.operation.TridentCollector)
	 */
	public void execute(TridentTuple tuple, TridentCollector collector) {
		try{

		ObjectModelOfKafka vehicleInfo = (ObjectModelOfKafka) tuple.getValueByField("vehicle");
	    String deviceid =vehicleInfo.getDEVICE_ID();
	    System.out.println("报警终端"+deviceid);
		if (deviceid!=null&&deviceid.length()>0) {
			
			collector.emit(new Values(deviceid));
		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	
}

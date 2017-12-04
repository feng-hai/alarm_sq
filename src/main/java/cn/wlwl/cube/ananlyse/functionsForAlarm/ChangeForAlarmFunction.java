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
import cn.wlwl.cube.analyse.bean.alart3G.Alert;
import cn.wlwl.cube.analyse.common.alarm.LatLng;
import cn.wlwl.cube.ananlyse.state.alarm.AMapConvertService;

/**
 * @ClassName: DeviceIDFunction
 * @Description: TODO找到终端id，并传递出去
 * @author fenghai
 * @date 2016年9月26日 上午11:09:56
 *
 */
public class ChangeForAlarmFunction extends BaseFunction {

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
		try {

			Alert alert = (Alert) tuple.getValueByField("vehicleInfo");
			// String deviceid =vehicleInfo.getDEVICE_ID();
			// if (deviceid!=null&&deviceid.length()>0) {
			LatLng latlng = AMapConvertService
					.getConvert(new LatLng(String.valueOf(alert.longitude), String.valueOf(alert.getLatitude())));
			// //System.out.println(latln
			alert.setLongitude(Double.parseDouble(latlng.getLng()));
			alert.setLatitude(Double.parseDouble(latlng.getLat()));

			collector.emit(new Values(alert));
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

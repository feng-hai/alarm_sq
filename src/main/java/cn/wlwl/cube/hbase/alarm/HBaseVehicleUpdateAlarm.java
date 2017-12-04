/**  
* @Title: HBaseVehicleUpdate.java
* @Package com.wlwl.cube.hbase
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年9月30日 上午11:27:24
* @version V1.0.0  
*/
package cn.wlwl.cube.hbase.alarm;

import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Map;

import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.operation.TridentOperationContext;
import org.apache.storm.trident.state.BaseStateUpdater;
import org.apache.storm.trident.tuple.TridentTuple;

import cn.wlwl.cube.analyse.bean.alart3G.Alert;


/**
 * @ClassName: HBaseVehicleUpdate
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fenghai
 * @param <VehicleStatisticBean>
 * @date 2016年9月30日 上午11:27:24
 *
 */
public class HBaseVehicleUpdateAlarm extends BaseStateUpdater<HBaseStateAlarm> {

	private List<Alert> vehicles;
	private long lastTime;

	@Override
	public void prepare(Map conf, TridentOperationContext context) {

		lastTime = System.currentTimeMillis();
		vehicles = new ArrayList<Alert>();
	}

	private static final long serialVersionUID = -3960567570106445647L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.storm.trident.state.StateUpdater#updateState(org.apache.storm.
	 * trident.state.State, java.util.List,
	 * org.apache.storm.trident.operation.TridentCollector)
	 */
	public void updateState(HBaseStateAlarm state, List<TridentTuple> tuples, TridentCollector collector) {
		long currentTime = System.currentTimeMillis();
	    System.out.println("开始提交报警");
		for (TridentTuple t : tuples) {
			Alert alert = (Alert) t.getValueByField("vehicleInfo");
			//
			// String key
			// =vehicle.getVehicle_unid()+StateUntils.formateDay(vehicle.getWorkTimeDateTime_end_t());
			// if (lastVehicles.containsKey(key)) {
			// VehicleStatisticBean vehicle_temp= lastVehicles.get(key);
			// vehicle.setWorkTimeDateTime_temp(vehicle.getWorkTimeDateTime_temp()+vehicle_temp.getWorkTimeDateTime_temp());
			// lastVehicles.replace(key, vehicle);
			// } else {
			//
			// lastVehicles.put(key, vehicle);
			//
			// }
			if (alert.getLongitude() > 0 && alert.getLatitude() > 0) {
//				LatLng latlng = AMapConvertService
//						.getConvert(new LatLng(String.valueOf(alert.longitude), String.valueOf(alert.getLatitude())));
//				// //System.out.println(latln
//				alert.setLongitude(Double.parseDouble(latlng.getLng()));
//				alert.setLatitude(Double.parseDouble(latlng.getLat()));

				vehicles.add(alert);
			}
		}

		if ( vehicles.size() > 0)

		{
			System.out.println("开始提交");
			// lastTime=currentTime;
			//
			// List<VehicleStatisticBean> vehiclesM = new
			// ArrayList<VehicleStatisticBean>();
			// Iterator iter = lastVehicles.entrySet().iterator();
			// while (iter.hasNext()) {
			// Map.Entry entry = (Map.Entry) iter.next();
			// Object val = entry.getValue();
			// vehiclesM.add((VehicleStatisticBean) val);
			// }
			// lastVehicles.clear();
			// state.setVehicleBulk(vehiclesM);
			lastTime = currentTime;
			Collections.reverse(vehicles);
			state.setVehicleBulk(vehicles);
			vehicles.clear();
		}

	}

}

/**  
* @Title: Split.java
* @Package com.wlwl.cube.ananlyse.functions
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年9月24日 下午1:01:54
* @version V1.0.0  
*/
package cn.wlwl.cube.ananlyse.functionsForAlarm;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.storm.trident.operation.BaseFunction;
import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;

import cn.wlwl.cube.analyse.bean.alarm.ObjectModelOfKafka;
import cn.wlwl.cube.ananlyse.state.alarm.JsonUtils;
import cn.wlwl.cube.ananlyse.state.alarm.StateUntils;

/**
 * @ClassName: Split
 * @Description: TODO从kafka中获取字符串，并反序列化为对象，传输出去
 * @author fenghai
 * @date 2016年9月24日 下午1:01:54
 *
 */
public class CreateVehicleModelFunction extends BaseFunction {
	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = -888818998763391563L;
	private ObjectModelOfKafka vehicle = null;

	public void execute(TridentTuple tuple, TridentCollector collector) {
		try {
			String sentence = tuple.getString(0);
			//System.out.println(sentence);
			
			sentence=	sentence.replace( "DEVICE_ID","device_ID")
			.replace("TIMESTAMP","timestamp")
			.replace( "IP4","ip4")
			.replace( "raw_octets","raw_OCTETS");

			vehicle = JsonUtils.deserialize(
					sentence,
					ObjectModelOfKafka.class);

			if (vehicle != null) {
				vehicle.setRAW_OCTETS(vehicle.getRAW_OCTETS().toUpperCase());
				collector.emit(new Values(vehicle));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
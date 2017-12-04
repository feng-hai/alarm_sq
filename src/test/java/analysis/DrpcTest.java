/**  
* @Title: DrpcTest.java
* @Package analysis
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年10月25日 下午2:22:50
* @version V1.0.0  
*/
package analysis;

import java.util.Date;

import org.apache.storm.Config;
import org.apache.storm.utils.DRPCClient;

import cn.wlwl.cube.analyse.bean.alarm.ObjectModelOfKafka;
import cn.wlwl.cube.analyse.bean.alart3G.OX038BAlert;
import cn.wlwl.cube.ananlyse.state.alarm.JsonUtils;

/**
 * @ClassName: DrpcTest
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fenghai
 * @date 2016年10月25日 下午2:22:50
 *
 */
public class DrpcTest {

	public static void main(String[] args) throws Exception {
//		Config conf = new Config();
//		ObjectModelOfKafka  kafka=new ObjectModelOfKafka();
//		kafka.setTIMESTAMP(new Date().getTime());
//		kafka.setRAW_OCTETS("7E000000008B0315001E054233303836350201008E123C07608DDA01010007CC070101000000000000CD0D7E");
//		OX038BAlert alert=new OX038BAlert(kafka);
		
		String sentence ="{\"unid\":\"0B6F4620E617460AA51B9F919A8D4B16\",\"proto_unid\":\"CD039E17A8E84137AF6DE1CDC172C274\",\"node_unid\":\"3CE0CF193D67408E80346E0C20263DC6\",\"length\":\"33\",\"cellphone\":\"34455\",\"flag_transmit\":\"false\",\"TIMESTAMP\":\"1493030448923\",\"IP4\":\"122.97.176.17\",\"raw_octets\":\"7e00000000e8022b00be01303130303338020100bd01000000000000000000000000000000001104180a282c00c0010000a1a71a0c0087d612000000000000011a7e\",\"DEVICE_ID\":\"010038\"}";

		sentence=	sentence.replace( "DEVICE_ID","device_ID")
				.replace("TIMESTAMP","timestamp")
				.replace( "IP4","ip4")
				.replace( "raw_octets","raw_OCTETS");

	ObjectModelOfKafka vehicle = JsonUtils.deserialize(
						sentence,
						ObjectModelOfKafka.class);
		

	}

}

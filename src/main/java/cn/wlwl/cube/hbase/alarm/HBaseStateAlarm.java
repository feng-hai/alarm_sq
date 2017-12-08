/**  
* @Title: HbaseState.java
* @Package com.wlwl.cube.hbase
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年9月30日 上午11:13:37
* @version V1.0.0  
*/
package cn.wlwl.cube.hbase.alarm;

import static cn.wlwl.cube.analyse.bean.alarm.GMSEvent.STATE_BEGIN;
import static cn.wlwl.cube.analyse.bean.alarm.GMSEvent.STATE_END;
import static cn.wlwl.cube.analyse.bean.alarm.GMSEvent.TYPE_SYSTEM_ERROR;
import static cn.wlwl.cube.analyse.bean.alarm.GMSEvent.TYPE_THRESHOLD_ERROR;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.storm.trident.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.wlwl.cube.analyse.bean.alarm.ErrorCode;
import cn.wlwl.cube.analyse.bean.alarm.GMSEvent;
import cn.wlwl.cube.analyse.bean.alarm.ObjectModelOfKafka;
import cn.wlwl.cube.analyse.bean.alarm.VehicleStatisticBean;
import cn.wlwl.cube.analyse.bean.alarm.VehicleStatusBean;
import cn.wlwl.cube.analyse.bean.alart3G.Alert;
import cn.wlwl.cube.analyse.common.alarm.Conf;
import cn.wlwl.cube.ananlyse.state.alarm.JsonUtils;
import cn.wlwl.cube.ananlyse.state.alarm.StateUntils;
import cn.wlwl.cube.ananlyse.state.alarm.TimeBaseRowStrategy;
import cn.wlwl.cube.ananlyse.state.alarm.UNID;
import cn.wlwl.cube.mysql.JdbcUtils;
import cn.wlwl.cube.mysql.SingletonJDBC;
import cn.wlwl.cube.redis.RedisSingleton;
import cn.wlwl.cube.redis.RedisUtils;

/**
 * @ClassName: HbaseState
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fenghai
 * @date 2016年9月30日 上午11:13:37
 *
 */
public class HBaseStateAlarm implements State {
	private static final String tableName = "DataAnalysis";

	private static final String aiid_key = "ALARM_AIID:";
	private static final String family = "count";
	private RedisUtils util = null;
	private JdbcUtils jdbcUtils = null;
	SimpleDateFormat DEFAULT_DATE_SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleDateFormat DEFAULT_DATE = new SimpleDateFormat("yyyyMM");
	private static final Logger LOG = LoggerFactory.getLogger(HBaseStateAlarm.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.storm.trident.state.State#beginCommit(java.lang.Long)
	 */
	public void beginCommit(Long txid) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.storm.trident.state.State#commit(java.lang.Long)
	 */
	public void commit(Long txid) {
		// TODO Auto-generated method stub

	}

	public void setVehicleBulk(List<Alert> alerts) {

		util = RedisSingleton.instance();
		jdbcUtils = SingletonJDBC.getJDBC();
		//System.out .println("报警数据："+JsonUtils.serialize(alerts));
		try {

			for (Alert alert : alerts) {
				
				List<GMSEvent> events = alert.getEvents();

				for (GMSEvent event : events) {

					if (event.getCode().equals("969999105") || event.getCode().equals("969999106")
							|| event.getCode().equals("969999107"))
						continue;// XXX

					if (event.getType() == TYPE_SYSTEM_ERROR || event.getType() == TYPE_THRESHOLD_ERROR) {
						if (event.getStatus() == STATE_BEGIN) {
							
						//	System.out.println("报警----------------01");
							String aiid = util.hget(aiid_key + alert.getUnid(), event.getCode());
							if (aiid != null) {
								event.setFlagEnd(true);
								event.setDatimeEnd(DEFAULT_DATE_SIMPLEDATEFORMAT.format(new Date()));
								alertEnd(event, alert, "alert");
							}
						//	System.out.println("报警----------------02");
							alertBegin(event, alert, "alert");
						//	System.out.println("报警----------------03");
						} else if (event.getStatus() == STATE_END)
						//	System.out.println("报警----------------04");
							alertEnd(event, alert, "alert");
						//	System.out.println("报警----------------05");
					}

				}

			}

			// HBaseUtils.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void alertEnd(GMSEvent event, Alert alert, String eventType) {
		String aiid = util.hget(aiid_key + alert.getUnid(), event.getCode());
		String dateStr = util.hget(aiid_key + alert.getUnid(), event.getCode() + "beginTime");
		if (aiid != null) {
			StringBuilder update = new StringBuilder();
			update.append("CALL `sensor`.`offAlarmting`('");
			update.append(aiid);
			update.append("', '");
			update.append(event.getDatimeEnd());
			update.append("', '");
			update.append(dateStr);
			update.append("')");
			try {
				jdbcUtils.updateByPreparedStatement(update.toString(), new ArrayList<Object>());
				System.out.println("更新到数据库中的【表情】"+aiid);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			util.hdel(aiid_key + alert.getUnid(), event.getCode());
			util.hdel(aiid_key + alert.getUnid(), event.getCode() + "beginTime");
		}
	}

	private void alertBegin(GMSEvent event, Alert alert, String eventType) {

		String alarmKey = Conf.VEHICLE_CONDITION_ALARM_STATUS;
		String unid = alert.getUnid(); // 车辆唯一标识//snapshot.getEntity().getUnid();
		String key = Conf.PERFIX + unid;
		String fiberId = util.hget(key, "fiber_unid");
		String listStr = util.get(alarmKey+fiberId + "_" + event.getCode());
		ErrorCode errorCode = null;// 获取故障代码库数据 //findErrorCode(
									// snapshot.getFiberUnid(), event.getCode()
									// );	
		//util.del(alarmKey);
		//util.del(alarmKey+"*");
		if (listStr != null) {
			errorCode = JsonUtils.deserialize(listStr, ErrorCode.class);
		} else {
			errorCode = setRedis(fiberId, event.getCode());
			if (errorCode != null) {
				util.setex(alarmKey+fiberId + "_" + event.getCode(), JsonUtils.serialize(errorCode),60*60*12);
			}
		}
		if (errorCode == null) {
			System.out.println(fiberId + "_" + event.getCode());
			return;
		}
		String domainId = util.hget(key, "domain_unid");
		//System.out.println("分組id"+domainId);
        
		try {
			
			System.out.println("新增到数据库中的【表情】开始插入");
			String alamUnid = UNID.getUnid();
			String tabeSuf = DEFAULT_DATE.format(new Date());
			String sql = "CALL `sensor`.`insertAlarmEvent`(?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?,?)";
			

			List<Object> params = new ArrayList<Object>();
			params.add(alamUnid);
			params.add(unid);
			params.add(domainId);
			params.add(event.getDatimeBegin());
			params.add(alert.getLongitude());
			params.add(alert.getLatitude());
			params.add(event.getCode());
			params.add(errorCode.getUNID());
			params.add(errorCode != null ? errorCode.getNAME() : "");
			params.add(eventType);
			params.add(event.getHex());
			params.add(errorCode != null ? errorCode.getLEVEL() : 0);
			params.add(tabeSuf);
			params.add(0);
			//System.out.println("CALL `sensor`.`insertAlarmEvent`("+alamUnid+", "+unid+", "+domainId+", "+event.getDatimeBegin()+", "+alert.getLongitude()+","+alert.getLatitude()+", "+event.getCode()+", "+errorCode.getUNID()+", "+errorCode != null ? errorCode.getNAME() : ""+", "+eventType+", "+event.getHex()+", "+errorCode.getLEVEL()+", "+tabeSuf+")");
			jdbcUtils.updateByPreparedStatement(sql, params);
			System.out.println("新增到数据库中的【表情】"+alamUnid);
			util.hset(aiid_key + unid, event.getCode(), String.valueOf(alamUnid));
			util.hset(aiid_key + unid, event.getCode() + "beginTime", tabeSuf);

		} catch (SQLException e) {
			LOG.error("插入数据库错误",e);
		} finally {
			
		}

		// long alertCount = 0;
		// ShardedJedis redis = REDIS_POOL.getResource();
		// if (redis != null)
		// {
		// final String alertCounterKey = ALERT_COUNTER_KEY_PREFIX + unid;
		// alertCount = redis.incr( alertCounterKey );
		// String redisKey = PREFIX_VEHICLE + unid;
		// redis.hset( redisKey, KEY_LEVEL, "" + (errorCode != null ?
		// errorCode.getLevel() : 5) );
		// redis.close();
		// }

		StringBuilder update = new StringBuilder();
		update.append("update sensor.ANA_SNAPSHOT set DATIME_ALERT=");
		update.append("'").append(event.getDatimeBegin()).append("'");
		update.append(",COUNT_ALERT=").append(alert.getEventCount());
		update.append(",LEVEL_ALERT=").append((errorCode != null ? errorCode.getLEVEL() : 5));
		update.append(",NODE_UNID='").append(alert.getNode_unid()).append("'");
		update.append(" where UNID='").append(unid).append("'");
		try {
			jdbcUtils.updateByPreparedStatement(update.toString(), new ArrayList<Object>());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOG.error("更新数据库错误",e);
		}
		// DBHelper.commitSQL( update.toString() );
	}

	/**
	 * @Title: updateCondition @Description: TODO固定的时间更新一下判断条件 @param
	 *         设定文件 @return void 返回类型 @throws
	 */
	// private void updateCondition() {
	// String timekey = Conf.VEHICLE_CONDITION_ALARM ;
	// String timer = util.hget(timekey, Conf.ACTIVE_CONDITION_TIMER);
	// util = RedisSingleton.instance();
	// // jdbcUtils = SingletonJDBC.getJDBC();
	// if (timer != null) {
	// Date date = StateUntils.strToDate(timer);
	// if (date != null) {
	// long m = new Date().getTime() - date.getTime();
	// if (m > 1000 * 60 * 5) {
	// util.hset(timekey, Conf.ACTIVE_CONDITION_TIMER, StateUntils.formate(new
	// Date()));
	// // 更新数据
	// String alarmKey = Conf.VEHICLE_CONDITION_ALARM_STATUS ;
	// Map<String, String> map = setRedis();
	// if (map.size() > 0) {
	// util.del(alarmKey);
	// util.hmset(alarmKey, map);
	// }
	// }
	// } else {
	// util.hset(timekey, Conf.ACTIVE_CONDITION_TIMER, StateUntils.formate(new
	// Date()));
	// }
	//
	// } else {
	// util.hset(timekey, Conf.ACTIVE_CONDITION_TIMER, StateUntils.formate(new
	// Date()));
	// }
	//
	// }

	/**
	 * @return @Title: setRedis @Description: TODO(这里用一句话描述这个方法的作用) @param
	 *         设定文件 @return void 返回类型 @throws
	 */
	private ErrorCode setRedis(String fiberId, String code) {

		String sql = "SELECT description,FIBER_UNID ,ERROR_CODE ,name,level ,unid  FROM cube.BIG_ERROR_CODE where fiber_unid='"
				+ fiberId + "' and error_code='" + code + "'";
		List<Object> params = new ArrayList<Object>();
		List<ErrorCode> list = new ArrayList<ErrorCode>();
		try {
			list = (List<ErrorCode>) jdbcUtils.findMoreRefResult(sql, params, ErrorCode.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ErrorCode error = null;
		if (list.size() > 0) {
			error = list.get(0);
		}
		return error;
	}

}

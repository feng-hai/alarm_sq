/**  
* @Title: HbaseState.java
* @Package com.wlwl.cube.hbase
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年9月30日 上午11:13:37
* @version V1.0.0  
*/
package com.wlwl.cube.hbase;

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

import org.apache.storm.trident.state.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wlwl.cube.analyse.bean.ErrorCode;
import com.wlwl.cube.analyse.bean.GMSEvent;
import com.wlwl.cube.analyse.bean.ObjectModelOfKafka;
import com.wlwl.cube.analyse.bean.VehicleStatisticBean;
import com.wlwl.cube.analyse.bean.VehicleStatusBean;
import com.wlwl.cube.analyse.bean.alart3G.Alert;
import com.wlwl.cube.analyse.common.Conf;

import com.wlwl.cube.ananlyse.state.JsonUtils;
import com.wlwl.cube.ananlyse.state.StateUntils;
import com.wlwl.cube.ananlyse.state.TimeBaseRowStrategy;
import com.wlwl.cube.ananlyse.state.UNID;
import com.wlwl.cube.mysql.JdbcUtils;
import com.wlwl.cube.mysql.SingletonJDBC;
import com.wlwl.cube.redis.RedisSingleton;
import com.wlwl.cube.redis.RedisUtils;

import static com.wlwl.cube.analyse.bean.GMSEvent.STATE_BEGIN;
import static com.wlwl.cube.analyse.bean.GMSEvent.STATE_END;
import static com.wlwl.cube.analyse.bean.GMSEvent.TYPE_SYSTEM_ERROR;
import static com.wlwl.cube.analyse.bean.GMSEvent.TYPE_THRESHOLD_ERROR;

/**
 * @ClassName: HbaseState
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author fenghai
 * @date 2016年9月30日 上午11:13:37
 *
 */
public class HBaseState implements State {
	private static final String tableName = "DataAnalysis";

	private static final String aiid_key = "ALARM_AIID:";
	private static final String family = "count";
	private RedisUtils util = null;
	private JdbcUtils jdbcUtils = null;
	SimpleDateFormat DEFAULT_DATE_SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Logger LOG = LoggerFactory.getLogger(HBaseState.class);

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
	
		try {

			for (Alert alert : alerts) {
				List<GMSEvent> events = alert.getEvents();

				for (GMSEvent event : events) {
					
					
					if (event.getCode().equals( "969999105" ) || event.getCode().equals( "969999106" )
							|| event.getCode().equals( "969999107" ))
						continue;// XXX

					if (event.getType() == TYPE_SYSTEM_ERROR || event.getType() == TYPE_THRESHOLD_ERROR)
					{
						if (event.getStatus() == STATE_BEGIN)
						{
							String aiid = util.hget(aiid_key + alert.getUnid(), event.getCode());
							if(aiid!=null)
							{
								event.setFlagEnd( true );
								event.setDatimeEnd(  DEFAULT_DATE_SIMPLEDATEFORMAT.format(new Date())  );
								alertEnd(event, alert, "alert");	
							}
							alertBegin(event, alert, "alert");
						}
						else
							if (event.getStatus() == STATE_END)
								alertEnd(event, alert, "alert");
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
		if (aiid != null) {
			StringBuilder update = new StringBuilder();
			update.append("update sensor.ANA_VEHICLE_EVENT set FLAG_DID=1,DATIME_END=");
			update.append("'").append(event.getDatimeEnd()).append("'");
			update.append(" where AIID=").append(aiid);

			try {
				jdbcUtils.updateByPreparedStatement(update.toString(), new ArrayList<Object>());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			util.hdel(aiid_key + alert.getUnid(), event.getCode());

		}
	}

	private void alertBegin(GMSEvent event, Alert alert, String eventType) {

		String alarmKey = Conf.VEHICLE_CONDITION_ALARM_STATUS;
		String unid = alert.getUnid(); // 车辆唯一标识//snapshot.getEntity().getUnid();
		String key = Conf.PERFIX + unid;

		String fiberId = util.hget(key, "fiber_unid");

		List<String> listStr = util.hmget(alarmKey, fiberId + "_" + event.getCode());
		ErrorCode errorCode = null;// 获取故障代码库数据 //findErrorCode(
									// snapshot.getFiberUnid(), event.getCode()
									// );
		if (listStr.size() > 0&&listStr.get(0)!=null) {
			errorCode = JsonUtils.deserialize(listStr.get(0), ErrorCode.class);
		} else {
			Map<String, String> map = setRedis();
			if (map.size() > 0) {
				util.del(alarmKey);
				util.hmset(alarmKey, map);
			}
			listStr = util.hmget(alarmKey, fiberId + "_" + event.getCode());
			errorCode = JsonUtils.deserialize(listStr.get(0), ErrorCode.class);
		}

		String domainId = util.hget(key, "domain_unid");

		
		try {
			// connection = jdbc.getConnection();
			String sql = "insert into sensor.ANA_VEHICLE_EVENT(UNID,ENTITY_UNID,DOMAIN_UNID,SUMMARY,EVENT_TYPE,LAT_D,LON_D,CONTEXT,LEVEL,ERROR_CODE) values(?,?,?,?,?,?,?,?,?,?)";

			List<Object> params = new ArrayList<Object>();
			params.add(UNID.getUnid());
			params.add(unid);
			params.add(domainId);
			params.add(errorCode != null ? errorCode.getNAME() : "");
			params.add(eventType);
			params.add(alert.getLatitude());
			params.add(alert.getLongitude());
			params.add(event.getHex());
			params.add(errorCode != null ? errorCode.getLEVEL() : 0);
			params.add(event.getCode());

			int aiid = jdbcUtils.insertByPreparedStatement(sql, params);

			util.hset(aiid_key + unid, event.getCode(), String.valueOf(aiid));

			// PreparedStatement pstmt = connection.prepareStatement( sql,
			// Statement.RETURN_GENERATED_KEYS );
			// pstmt.setString( 1, UNID.getUnid() );
			// pstmt.setString( 2, unid );
			// pstmt.setString( 3, snapshot.getEntity().getDomainUnid() );
			// pstmt.setString( 4, errorCode != null ? errorCode.getName() : ""
			// );
			// pstmt.setString( 5, eventType );
			// pstmt.setDouble( 6, snapshot.getLatitudeDeviated() );
			// pstmt.setDouble( 7, snapshot.getLongitudeDeviated() );
			// pstmt.setString( 8, event.getHex() );// TODO
			// pstmt.setInt( 9, errorCode != null ? errorCode.getLevel() : 0 );
			// pstmt.setString( 10, event.getCode() );// TODO
			//
			// //((Object) LOG).fine( "insert event: " + pstmt.toString() );
			// pstmt.execute();
			// ResultSet rs = pstmt.getGeneratedKeys();
			// if (rs.next())
			// {
			// int aiid = rs.getInt( 1 );
			// LOG.info( "alert id:" + aiid );
			// AlertEvent alert = new AlertEvent();
			// alert.setAiid( aiid );
			// alert.setDatimeBegin( DateHelper.getDateYYYY_MM_DD() );
			// alert.setEntityUnid( unid );
			// alert.setEventType( Event.EVENT_ALERT );
			// alert.setTag( event.getCode() );
			// String alertKey = unid + "_" + aiid;
			// addEvent( snapshot, alert );
			// }
			// pstmt.close();
		} catch (SQLException e) {
			// LOG.severe( "get connection锛� " + e.getLocalizedMessage() );
		} finally {
			/// closeConnection( connection );
			// jdbc.releaseConn();
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
			e.printStackTrace();
		}
		// DBHelper.commitSQL( update.toString() );
	}

	/**
	 * @Title: updateCondition @Description: TODO固定的时间更新一下判断条件 @param
	 *         设定文件 @return void 返回类型 @throws
	 */
	private void updateCondition() {
		String timekey = Conf.VEHICLE_CONDITION_ALARM ;
		String timer = util.hget(timekey, Conf.ACTIVE_CONDITION_TIMER);
		util = RedisSingleton.instance();
		// jdbcUtils = SingletonJDBC.getJDBC();
		if (timer != null) {
			Date date = StateUntils.strToDate(timer);
			if (date != null) {
				long m = new Date().getTime() - date.getTime();
				if (m > 1000 * 60 * 2) {
					util.hset(timekey, Conf.ACTIVE_CONDITION_TIMER, StateUntils.formate(new Date()));
					// 更新数据
					String alarmKey = Conf.VEHICLE_CONDITION_ALARM_STATUS ;
					Map<String, String> map = setRedis();
					if (map.size() > 0) {
						util.del(alarmKey);
						util.hmset(alarmKey, map);
					}
				}
			} else {
				util.hset(timekey, Conf.ACTIVE_CONDITION_TIMER, StateUntils.formate(new Date()));
			}

		} else {
			util.hset(timekey, Conf.ACTIVE_CONDITION_TIMER, StateUntils.formate(new Date()));
		}

	}

	/**
	 * @return @Title: setRedis @Description: TODO(这里用一句话描述这个方法的作用) @param
	 *         设定文件 @return void 返回类型 @throws
	 */
	private Map<String, String> setRedis() {

		String sql = "SELECT description,FIBER_UNID ,ERROR_CODE ,name,level   FROM cube.BIG_ERROR_CODE";
		List<Object> params = new ArrayList<Object>();

		List<ErrorCode> list = null;
		try {
			list = (List<ErrorCode>) jdbcUtils.findMoreRefResult(sql, params, ErrorCode.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOG.debug("数据库中数据" + list);
		Map<String, String> map = new HashMap<String, String>();
		for (ErrorCode vsbean : list) {
			map.put(vsbean.getFIBER_UNID() + "_" + vsbean.getERROR_CODE(), JsonUtils.serialize(vsbean));
		}

		return map;

	}

}

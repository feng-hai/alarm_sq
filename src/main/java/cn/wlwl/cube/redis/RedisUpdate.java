/**  
* @Title: RedisUpdate.java
* @Package com.wlwl.cube.redis
* @Description: TODO(用一句话描述该文件做什么)
* @author fenghai  
* @date 2016年10月20日 下午10:36:08
* @version V1.0.0  
*/ 
package cn.wlwl.cube.redis;

import java.util.List;

import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.state.BaseStateUpdater;
import org.apache.storm.trident.tuple.TridentTuple;

/**
* @ClassName: RedisUpdate
* @Description: TODO(这里用一句话描述这个类的作用)
* @author fenghai
* @date 2016年10月20日 下午10:36:08
*
*/
public class RedisUpdate extends BaseStateUpdater<RedisState_U> {

	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/ 
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see org.apache.storm.trident.state.StateUpdater#updateState(org.apache.storm.trident.state.State, java.util.List, org.apache.storm.trident.operation.TridentCollector)
	 */
	public void updateState(RedisState_U state, List<TridentTuple> tuples, TridentCollector collector) {
		// TODO Auto-generated method stub
		
	}

}

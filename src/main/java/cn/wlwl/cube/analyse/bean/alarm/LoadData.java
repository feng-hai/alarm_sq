package cn.wlwl.cube.analyse.bean.alarm;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import cn.wlwl.cube.mysql.JdbcUtils;
import cn.wlwl.cube.mysql.SingletonJDBC;



public class LoadData extends TimerTask {

	//private static SessionManager _sesionM;

	public LoadData() {
		
	}

	
	@Override
	public void run() {
		// 查询数据库
		System.out.println("数据加载");
	}
	
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
			JdbcUtils jdbcUtils = SingletonJDBC.getJDBC();
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

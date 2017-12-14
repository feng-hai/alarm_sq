package cn.wlwl.cube.kafka;





import cn.wlwl.cube.analyse.bean.alarm.AlarmInfo;

public class HandlerProducer implements Runnable {

	private AlarmInfo message;

	public HandlerProducer(AlarmInfo message) {
		this.message = message;
	}

	@Override
	public void run() {
		KafkaProducerSingleton kafkaProducerSingleton = KafkaProducerSingleton.getInstance();
		//HashMap<String, String> config = PropertyResource.getInstance().getProperties();
		kafkaProducerSingleton.init("alarmTopic", 1);
		//System.out.println("当前线程:" + Thread.currentThread().getName() + ",获取的kafka实例:" + kafkaProducerSingleton);
		kafkaProducerSingleton.sendKafkaMessage(message);
	}

}

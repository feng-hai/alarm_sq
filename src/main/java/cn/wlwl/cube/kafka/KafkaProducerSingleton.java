package cn.wlwl.cube.kafka;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Properties;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.wlwl.cube.analyse.bean.alarm.AlarmInfo;
import cn.wlwl.cube.analyse.common.alarm.Conf;


public final class KafkaProducerSingleton {

	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerSingleton.class);

	private static KafkaProducer<String, String> kafkaProducer;

//	private Random random = new Random();

	private String topic;

	private int retry;

	private KafkaProducerSingleton() {

	}

	/**
	 * 静态内部类
	 * 
	 * @author tanjie
	 * 
	 */
	private static class LazyHandler {

		private static final KafkaProducerSingleton instance = new KafkaProducerSingleton();
	}

	/**
	 * 单例模式,kafkaProducer是线程安全的,可以多线程共享一个实例
	 * 
	 * @return
	 */
	public static final KafkaProducerSingleton getInstance() {
		return LazyHandler.instance;
	}

	/**
	 * kafka生产者进行初始化
	 * 
	 * @return KafkaProducer
	 */
	public void init(String topic, int retry) {
		this.topic = topic;
		this.retry = retry;
		if (null == kafkaProducer) {
			
				//HashMap<String, String> config = PropertyResource.getInstance().getProperties();
				Properties props = new Properties();
				props.put("bootstrap.servers",Conf.BROKERURL);
				//props.put("acks", "1");
				//props.put("retries", 0);
				//props.put("producer.type","async"); 
			//	props.put("batch.size", 65536);
				//props.put("linger.ms", 5);
				//props.put("buffer.memory", 4194304);
				props.put("acks", "1");
				props.put("retries", 0);
				props.put("batch.size", 16384);
				props.put("linger.ms", 1);
				props.put("buffer.memory", 33554432);
				props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
				props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
				
				
				
				
				kafkaProducer = new KafkaProducer<String, String>(props);
			
		}
	}

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	/**
	 * 通过kafkaProducer发送消息
	 * 
	 * @param topic
	 *            消息接收主题
	 * @param partitionNum
	 *            哪一个分区
	 * @param retry
	 *            重试次数
	 * @param message
	 *            具体消息值
	 */
	public void sendKafkaMessage(final AlarmInfo message) {
		/**
		 * 1、如果指定了某个分区,会只讲消息发到这个分区上 2、如果同时指定了某个分区和key,则也会将消息发送到指定分区上,key不起作用
		 * 3、如果没有指定分区和key,那么将会随机发送到topic的分区中 4、如果指定了key,那么将会以hash<key>的方式发送到分区中
		 */
		
//		ObjectModelOfKafka temPMessage=new ObjectModelOfKafka();
//		temPMessage.setDATIME_RX(sdf.format(message.getDatetime()));
//		temPMessage.setPairs(message.getPairs());
//		temPMessage.setTIMESTAMP(message.getDatetime());
		
		ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic, message.getUnid(),
				message.toString());
		// send方法是异步的,添加消息到缓存区等待发送,并立即返回，这使生产者通过批量发送消息来提高效率
		// kafka生产者是线程安全的,可以单实例发送消息
		kafkaProducer.send(record, new Callback() {
			public void onCompletion(RecordMetadata recordMetadata, Exception exception) {
				if (null != exception) {
					LOGGER.error("kafka发送消息失败:" + exception.getMessage(), exception);
					//retryKakfaMessage(temPMessage, message.getVehicleUnid());
					kafkaProducer=null;
				}
			}
		}
		);
	}

	/**
	 * 当kafka消息发送失败后,重试
	 * 
	 * @param retryMessage
	 */
//	private void retryKakfaMessage(final ObjectModelOfKafka retryMessage,String key) {
//		
//		ProducerRecord<String, String> record = new ProducerRecord<String, String>(topic,key ,
//				retryMessage.toString());
//		for (int i = 1; i <= retry; i++) {
//			try {
//				kafkaProducer.send(record);
//				return;
//			} catch (Exception e) {
//				LOGGER.error("kafka发送消息失败:" + e.getMessage(), e);
//				retryKakfaMessage(retryMessage,key);
//			}
//		}
//	}

	/**
	 * kafka实例销毁
	 */
	public void close() {
		if (null != kafkaProducer) {
			kafkaProducer.close();
		}
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getRetry() {
		return retry;
	}

	public void setRetry(int retry) {
		this.retry = retry;
	}

}

/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2016 Ttron Kidman. All rights reserved.
 */
package cn.wlwl.cube.analyse.bean.alart3G;

import static cn.wlwl.cube.analyse.bean.alarm.GMSEvent.STATE_BEGIN;
import static cn.wlwl.cube.analyse.bean.alarm.GMSEvent.STATE_END;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import cn.wlwl.cube.analyse.bean.alarm.GMSEvent;
import cn.wlwl.cube.analyse.bean.alarm.ObjectModelOfKafka;
import cn.wlwl.cube.analyse.common.alarm.LatLng;
import cn.wlwl.cube.ananlyse.state.alarm.AMapConvertService;
import cn.wlwl.cube.ananlyse.state.alarm.BCDUtils;
import cn.wlwl.cube.ananlyse.state.alarm.ByteUtils;

/**
 * @Ttron 2016年4月1日
 */
public class OX038BAlert extends Alert {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static final double WEIGHT = 1e6;

	private String RAW_OCTETS;
	private Date daTime;

	// public OX02E7Alert(byte[] octets)
	// {
	// super( octets );
	// }

	public OX038BAlert(ObjectModelOfKafka heartbeat) {
		String orc = heartbeat.getRAW_OCTETS();
		daTime=new Date(heartbeat.getTIMESTAMP());
		orc = orc.substring(2, orc.length() - 2);
		orc = orc.replaceAll("7D01", "7E").replaceAll("7D02", "7D");
		this.RAW_OCTETS = orc;
		try {
			parsePrivateOctets();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<GMSEvent> getEvents() {
		return events;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	protected byte[] packPrivateOctets() {
		ByteBuffer buffer = ByteBuffer.allocate(128);// FIXME
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		buffer.putInt((int) (longitude * WEIGHT));
		buffer.putInt((int) (latitude * WEIGHT));
		buffer.put((byte) eventCount);

		buffer.flip();
		byte[] privateOctets = new byte[buffer.remaining()];
		buffer.get(privateOctets);
		return privateOctets;
	}

	public void parsePrivateOctets() throws Exception {
		int offset = 19;
		byte[] payload = ByteUtils.hexStr2Bytes(RAW_OCTETS);
		longitude = ByteUtils.getInt(payload, offset) / WEIGHT;
		latitude = ByteUtils.getInt(payload, offset + 4) / WEIGHT;
//		LatLng latlng = AMapConvertService.getConvert(new LatLng(String.valueOf(longitude), String.valueOf(latitude)));
//		longitude = Double.parseDouble(latlng.getLng());
//		latitude = Double.parseDouble(latlng.getLat());
		PointDouble pd = new PointDouble(longitude,latitude);
		PointDouble en = Wars2Wgs.s2c(pd);
		longitude=en.x;
		latitude=en.y;
		eventCount = payload[offset + 8];
		SimpleDateFormat DEFAULT_DATE_SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// eventCount = fromByteToInt( octets[offset] );
		if (eventCount > 0) {
			events = new LinkedList<GMSEvent>();
			int cursor = 9;
			boolean loop = true;
			do {
				byte[] array = new byte[12];
				if (payload.length - (offset + cursor) >= 12) {
					GMSEvent event = new GMSEvent();
					System.arraycopy(payload, offset + cursor, array, 0, 12);
					byte type = payload[offset + cursor];
					if(type==0)
					{
						int fmi = (int)( payload[offset + cursor + 4] & 0xFF);
						int spn =  ByteUtils.getIntForThree(payload, offset + cursor + 1);
						event.setCode(spn + "-" + fmi );
					}
					else
					{
					   int code = ByteUtils.getInt(payload, offset + cursor + 1);
					   event.setCode(Integer.toString(code));
					}
					byte status = payload[offset + cursor + 5];
					//Date datime = BCDUtils.bytesToDate(ByteUtils.getSubBytes(payload, offset + cursor + 5, 6));// fromCompressedBCDArrayToString(
				
					event.setHex(ByteUtils.byte2HexStr(array));
					event.setType(type);
					event.setStatus(status);
					
					events.add(event);
					if (status == STATE_BEGIN) {
						event.setDatimeBegin(DEFAULT_DATE_SIMPLEDATEFORMAT.format(daTime));
					} else if (status == STATE_END) {
						event.setFlagEnd(true);
						event.setDatimeEnd(DEFAULT_DATE_SIMPLEDATEFORMAT.format(daTime));

					}
				} else {
					loop = false;
				}

				if (payload.length - offset - cursor - 12 <= 3)
					loop = false;
				cursor += 12;
			} while (loop);
		}
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
}

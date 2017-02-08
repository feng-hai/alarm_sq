/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2016 Ttron Kidman. All rights reserved.
 */
package com.wlwl.cube.analyse.bean.alart3G;

import static com.wlwl.cube.analyse.bean.GMSEvent.STATE_BEGIN;
import static com.wlwl.cube.analyse.bean.GMSEvent.STATE_END;
//import static cn.ttron.protocol.util.ProtocolHelper.fromByteArrayToHexString;
//import static cn.ttron.protocol.util.ProtocolHelper.fromByteArrayToLongLittleEndian;
//import static cn.ttron.protocol.util.ProtocolHelper.fromByteToInt;
//import static cn.ttron.protocol.util.ProtocolHelper.fromCompressedBCDArrayToString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//import cn.ttron.protocol.ProtocolException;
import com.wlwl.cube.analyse.bean.GMSEvent;
import com.wlwl.cube.analyse.bean.ObjectModelOfKafka;
import com.wlwl.cube.analyse.common.LatLng;
import com.wlwl.cube.ananlyse.state.AMapConvertService;
import com.wlwl.cube.ananlyse.state.BCDUtils;
import com.wlwl.cube.ananlyse.state.ByteUtils;
//import cn.ttron.protocol.gimis.Part;
//import cn.ttron.protocol.gimis.five3g.GMS3GProtocol;

/**
 * @Ttron 2016年4月1日
 */
public class OX02E7Alert extends Alert
{
	protected static final double WEIGHT = 1e6;


	private String RAW_OCTETS;
	
	


//	public OX02E7Alert(byte[] octets)
//	{
//		super( octets );
//	}


	public OX02E7Alert(ObjectModelOfKafka heartbeat)
	{
		String orc=heartbeat.getRAW_OCTETS();
		orc= orc.substring(2,orc.length()-2);
		orc=orc.replaceAll("7D01", "7E").replaceAll("7D02", "7D");
		this.RAW_OCTETS=orc;
		try {
			parsePrivateOctets();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public List<GMSEvent> getEvents()
	{
		return events;
	}


	public double getLatitude()
	{
		return latitude;
	}


	public double getLongitude()
	{
		return longitude;
	}


	
	public byte[] packPrivateOctets()
	{
		ByteBuffer buffer = ByteBuffer.allocate( 128 );// FIXME
		buffer.order( ByteOrder.LITTLE_ENDIAN );
		buffer.putInt( (int) (longitude * WEIGHT) );
		buffer.putInt( (int) (latitude * WEIGHT) );
		buffer.put( (byte) eventCount );

		buffer.flip();
		byte[] privateOctets = new byte[buffer.remaining()];
		buffer.get( privateOctets );
		return privateOctets;
	}


   
	public  void parsePrivateOctets() throws Exception
	{
		int offset=19;
		byte[] payload=ByteUtils.hexStr2Bytes(RAW_OCTETS);
		longitude = ByteUtils.getInt(payload, offset) / WEIGHT;
		latitude = ByteUtils.getInt(payload, offset+4) / WEIGHT;
		LatLng latlng=AMapConvertService.getConvert(new LatLng(String.valueOf(longitude),String.valueOf(latitude)));
		//System.out.println(latlng);
		longitude=Double.parseDouble(latlng.getLng());
		latitude=Double.parseDouble(latlng.getLat());
		eventCount = payload[offset + 8] ;
		SimpleDateFormat DEFAULT_DATE_SIMPLEDATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	//	eventCount = fromByteToInt( octets[offset] );
		if (eventCount > 0)
		{
			events = new LinkedList<GMSEvent>();
			int cursor = 9;
			boolean loop = true;
			do
			{
				byte[] array = new byte[12];
				System.arraycopy( payload, offset + cursor, array, 0, 12 );
				byte type = payload[offset + cursor];
				int code =ByteUtils.getInt(  payload, offset + cursor + 1 );
				byte status = payload[offset + cursor + 5];
				
				Date datime =BCDUtils.bytesToDate(ByteUtils.getSubBytes(payload, offset + cursor + 5, 6));// fromCompressedBCDArrayToString( octets, offset + cursor + 5, 6 );

				GMSEvent event = new GMSEvent();
				event.setHex( ByteUtils.byte2HexStr(array) );

				event.setType( type );
				event.setStatus( status );
				event.setCode( Integer.toString( code ) );
				events.add( event );

				if (status == STATE_BEGIN)
				{
					event.setDatimeBegin( DEFAULT_DATE_SIMPLEDATEFORMAT.format(datime) );
				}
				else

					if (status == STATE_END)
					{
						event.setFlagEnd( true );
						event.setDatimeEnd(  DEFAULT_DATE_SIMPLEDATEFORMAT.format(datime)  );
					}

				if (payload.length - offset - cursor - 12 <= 3)
					loop = false;
				cursor += 12;
			} while (loop);
		}
	}


	public void setLatitude(double latitude)
	{
		this.latitude = latitude;
	}


	public void setLongitude(double longitude)
	{
		this.longitude = longitude;
	}
}

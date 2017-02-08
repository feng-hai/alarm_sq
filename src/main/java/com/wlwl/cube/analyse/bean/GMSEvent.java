/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2016 Ttron Kidman. All rights reserved.
 */
package com.wlwl.cube.analyse.bean;

/**
 * @Ttron 2016年4月1日
 */
public class GMSEvent
{
	public static final byte STATE_BEGIN = 0x01;

	public static final byte STATE_END = 0x00;

	public static final byte TYPE_THRESHOLD_ERROR = 0x01;

	public static final byte TYPE_SYSTEM_ERROR = 0x00;

	private String code;

	private String datimeBegin;

	private String datimeEnd;

	private boolean flagEnd = false;

	private String hex;

	private byte status = STATE_BEGIN;

	private byte type = TYPE_SYSTEM_ERROR;


	public String getCode()
	{
		return code;
	}


	public void setCode(String code)
	{
		this.code = code;
	}


	public String getDatimeBegin()
	{
		return datimeBegin;
	}


	public void setDatimeBegin(String datimeBegin)
	{
		this.datimeBegin = datimeBegin;
	}


	public String getDatimeEnd()
	{
		return datimeEnd;
	}


	public void setDatimeEnd(String datimeEnd)
	{
		this.datimeEnd = datimeEnd;
	}


	public boolean isFlagEnd()
	{
		return flagEnd;
	}


	public void setFlagEnd(boolean flagEnd)
	{
		this.flagEnd = flagEnd;
	}


	public String getHex()
	{
		return hex;
	}


	public void setHex(String hex)
	{
		this.hex = hex;
	}


	public byte getStatus()
	{
		return status;
	}


	public void setStatus(byte status)
	{
		this.status = status;
	}


	public byte getType()
	{
		return type;
	}


	public void setType(byte type)
	{
		this.type = type;
	}


	@Override
	public String toString()
	{
		return "GMSEvent [type=" + type + ", code=" + code + ", status=" + status + ", datimeBegin=" + datimeBegin + ", flagEnd="
				+ flagEnd + ", hex=" + hex + "]";
	}
}

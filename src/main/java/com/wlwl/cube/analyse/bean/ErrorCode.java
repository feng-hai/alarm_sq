/**
ha * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2007-2016 Ttron Kidman. All rights reserved.
 */
package com.wlwl.cube.analyse.bean;

import java.io.Serializable;

/**
 * @Ttron 2016年3月1日
 */

public class ErrorCode implements Serializable {

	private static final long serialVersionUID = 1L;
	private String DESCRIPTION;
	private String ERROR_CODE;
	private int LEVEL;
	private String NAME;
	private String UNID;
	public String getUNID() {
		return UNID;
	}



	public void setUNID(String uNID) {
		UNID = uNID;
	}



	public int getLEVEL() {
		return LEVEL;
	}

	

	public String getNAME() {
		return NAME;
	}

	public void setNAME(String nAME) {
		NAME = nAME;
	}

	public void setLEVEL(int lEVEL) {
		LEVEL = lEVEL;
	}

	public String getERROR_CODE() {
		return ERROR_CODE;
	}

	public void setERROR_CODE(String eRROR_CODE) {
		ERROR_CODE = eRROR_CODE;
	}

	private boolean flagAva = false;

	private String FIBER_UNID;

	public String getFIBER_UNID() {
		return FIBER_UNID;
	}

	public void setFIBER_UNID(String fIBER_UNID) {
		FIBER_UNID = fIBER_UNID;
	}

	private String part;

	public String getDescription() {
		return DESCRIPTION;
	}

	public String getPart() {
		return part;
	}

	public boolean isFlagAva() {
		return flagAva;
	}

	public void setDescription(String description) {
		this.DESCRIPTION = description;
	}

	public void setPart(String part) {
		this.part = part;
	}
}

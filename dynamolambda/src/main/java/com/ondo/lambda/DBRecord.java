package com.ondo.lambda;

import org.apache.commons.beanutils.BeanUtils;

/**
 * This DBRecord maps to the dashboard table in the DynamoDB
 */

public class DBRecord {
	/**
	 * This is Primary Key of the table
	 */
	private String id;
	private Double altBL;
	private Double altSpike;
	private Double altTH;
	private String bandId;
	private Integer bandSt;
	private Double curTemp;
	private Long curTime;
	private String displayName;
	private String facTimeZone;
	private String fcltyId;
	private String fName;
	private String grpId;
	private String lName;
	private Long notifiedAt;
	private String testTime;
	private Long ttl;
	private Integer wearerId;
	private Integer wearerSt;
	private Double batVolt;
	private Float ambTemp;
	private Integer accValue;

	private Integer fwVersion;
	private Integer rssi;
	private String gatewayBLEMacId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getAltBL() {
		return altBL;
	}

	public void setAltBL(Double altBL) {
		this.altBL = altBL;
	}

	public Double getAltSpike() {
		return altSpike;
	}

	public void setAltSpike(Double altSpike) {
		this.altSpike = altSpike;
	}

	public Double getAltTH() {
		return altTH;
	}

	public void setAltTH(Double altTH) {
		this.altTH = altTH;
	}

	public String getBandId() {
		return bandId;
	}

	public void setBandId(String bandId) {
		this.bandId = bandId;
	}

	public Integer getBandSt() {
		return bandSt;
	}

	public void setBandSt(Integer bandSt) {
		this.bandSt = bandSt;
	}

	public Double getCurTemp() {
		return curTemp;
	}

	public void setCurTemp(Double curTemp) {
		this.curTemp = curTemp;
	}

	public Long getCurTime() {
		return curTime;
	}

	public void setCurTime(Long curTime) {
		this.curTime = curTime;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getFacTimeZone() {
		return facTimeZone;
	}

	public void setFacTimeZone(String facTimeZone) {
		this.facTimeZone = facTimeZone;
	}

	public String getFcltyId() {
		return fcltyId;
	}

	public void setFcltyId(String fcltyId) {
		this.fcltyId = fcltyId;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getGrpId() {
		return grpId;
	}

	public void setGrpId(String grpId) {
		this.grpId = grpId;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public Long getNotifiedAt() {
		return notifiedAt;
	}

	public void setNotifiedAt(Long notifiedAt) {
		this.notifiedAt = notifiedAt;
	}

	public String getTestTime() {
		return testTime;
	}

	public void setTestTime(String testTime) {
		this.testTime = testTime;
	}

	public Long getTtl() {
		return ttl;
	}

	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}

	public Integer getWearerId() {
		return wearerId;
	}

	public void setWearerId(Integer wearerId) {
		this.wearerId = wearerId;
	}

	public Integer getWearerSt() {
		return wearerSt;
	}

	public void setWearerSt(Integer wearerSt) {
		this.wearerSt = wearerSt;
	}

	public Double getBatVolt() {
		return batVolt;
	}

	public void setBatVolt(Double batVolt) {
		this.batVolt = batVolt;
	}

	public Float getAmbTemp() {
		return ambTemp;
	}

	public void setAmbTemp(Float ambTemp) {
		this.ambTemp = ambTemp;
	}

	public Integer getAccValue() {
		return accValue;
	}

	public void setAccValue(Integer accValue) {
		this.accValue = accValue;
	}

	public Integer getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(Integer fwVersion) {
		this.fwVersion = fwVersion;
	}

	public Integer getRssi() {
		return rssi;
	}

	public void setRssi(Integer rssi) {
		this.rssi = rssi;
	}

	public String getGatewayBLEMacId() {
		return gatewayBLEMacId;
	}

	public void setGatewayBLEMacId(String gatewayBLEMacId) {
		this.gatewayBLEMacId = gatewayBLEMacId;
	}

	public static class Builder {
		private String id;
		private Double altBL;
		private Double altSpike;
		private Double altTH;
		private String bandId;
		private Integer bandSt;
		private Double curTemp;
		private Long curTime;
		private String displayName;
		private String facTimeZone;
		private String fcltyId;
		private String fName;
		private String grpId;
		private String lName;
		private Long notifiedAt;
		private String testTime;
		private Long ttl;
		private Integer wearerId;
		private Integer wearerSt;
		private Double batVolt;
		private Float ambTemp;
		private Integer accValue;

		private Integer fwVersion;
		private Integer rssi;
		private String gatewayBLEMacId;

		public String getId() {
			return id;
		}

		public Builder setId(String id) {
			this.id = id;
			return this;
		}

		public Double getAltBL() {
			return altBL;
		}

		public void setAltBL(Double altBL) {
			this.altBL = altBL;
		}

		public Double getAltSpike() {
			return altSpike;
		}

		public void setAltSpike(Double altSpike) {
			this.altSpike = altSpike;
		}

		public Double getAltTH() {
			return altTH;
		}

		public Builder setAltTH(Double altTH) {
			this.altTH = altTH;
			return this;
		}

		public String getBandId() {
			return bandId;
		}

		public Builder setBandId(String bandId) {
			this.bandId = bandId;
			return this;
		}

		public Integer getBandSt() {
			return bandSt;
		}

		public void setBandSt(Integer bandSt) {
			this.bandSt = bandSt;
		}

		public Double getCurTemp() {
			return curTemp;
		}

		public Builder setCurTemp(Double curTemp) {
			this.curTemp = curTemp;
			return this;
		}

		public Long getCurTime() {
			return curTime;
		}

		public Builder setCurTime(Long curTime) {
			this.curTime = curTime;
			return this;
		}

		public String getDisplayName() {
			return displayName;
		}

		public void setDisplayName(String displayName) {
			this.displayName = displayName;
		}

		public String getFacTimeZone() {
			return facTimeZone;
		}

		public void setFacTimeZone(String facTimeZone) {
			this.facTimeZone = facTimeZone;
		}

		public String getFcltyId() {
			return fcltyId;
		}

		public Builder setFcltyId(String fcltyId) {
			this.fcltyId = fcltyId;
			return this;
		}

		public String getfName() {
			return fName;
		}

		public Builder setfName(String fName) {
			this.fName = fName;
			return this;

		}

		public String getGrpId() {
			return grpId;
		}

		public Builder setGrpId(String grpId) {
			this.grpId = grpId;
			return this;

		}

		public String getlName() {
			return lName;
		}

		public Builder setlName(String lName) {
			this.lName = lName;
			return this;
		}

		public Long getNotifiedAt() {
			return notifiedAt;
		}

		public void setNotifiedAt(Long notifiedAt) {
			this.notifiedAt = notifiedAt;
		}

		public String getTestTime() {
			return testTime;
		}

		public void setTestTime(String testTime) {
			this.testTime = testTime;
		}

		public Long getTtl() {
			return ttl;
		}

		public void setTtl(Long ttl) {
			this.ttl = ttl;
		}

		public Integer getWearerId() {
			return wearerId;
		}

		public Builder setWearerId(Integer wearerId) {
			this.wearerId = wearerId;
			return this;
		}

		public Integer getWearerSt() {
			return wearerSt;
		}

		public void setWearerSt(Integer wearerSt) {
			this.wearerSt = wearerSt;
		}

		public Double getBatVolt() {
			return batVolt;
		}

		public Builder setBatVolt(Double batVolt) {
			this.batVolt = batVolt;
			return this;
		}

		public Float getAmbTemp() {
			return ambTemp;
		}

		public Builder setAmbTemp(Float ambTemp) {
			this.ambTemp = ambTemp;
			return this;
		}

		public Integer getAccValue() {
			return accValue;
		}

		public Builder setAccValue(Integer accValue) {
			this.accValue = accValue;
			return this;
		}

		public Integer getFwVersion() {
			return fwVersion;
		}

		public Builder setFwVersion(Integer fwVersion) {
			this.fwVersion = fwVersion;
			return this;
		}

		public Integer getRssi() {
			return rssi;
		}

		public Builder setRssi(Integer rssi) {
			this.rssi = rssi;
			return this;
		}

		public String getGatewayBLEMacId() {
			return gatewayBLEMacId;
		}

		public Builder setGatewayBLEMacId(String gatewayBLEMacId) {
			this.gatewayBLEMacId = gatewayBLEMacId;
			return this;
		}

		public DBRecord build() throws Exception  {
			DBRecord dbRecord = new DBRecord();
			 BeanUtils.copyProperties(dbRecord,this);
			return dbRecord;
		}

	}

	public static Builder builder() {

		return new Builder();
	}

}

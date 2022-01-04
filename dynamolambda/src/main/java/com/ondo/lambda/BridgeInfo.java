package com.ondo.lambda;

import com.fasterxml.jackson.annotation.JsonProperty;



public class BridgeInfo {
	
	@JsonProperty("bridgeId")
	private String bridgeId;
	
	@JsonProperty("facilityId")
	private String facilityId;
	
	@JsonProperty("bleMacId")
	private String bleMacId;
	
	@JsonProperty("bridgeName")
	private String bridgeName;

	@JsonProperty("lastHeartBeatTime")
	private Long lastHeartBeatTime;

	public String getBridgeId() {
		return bridgeId;
	}

	public void setBridgeId(String bridgeId) {
		this.bridgeId = bridgeId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getBleMacId() {
		return bleMacId;
	}

	public void setBleMacId(String bleMacId) {
		this.bleMacId = bleMacId;
	}

	public String getBridgeName() {
		return bridgeName;
	}

	public void setBridgeName(String bridgeName) {
		this.bridgeName = bridgeName;
	}

	public Long getLastHeartBeatTime() {
		return lastHeartBeatTime;
	}

	public void setLastHeartBeatTime(Long lastHeartBeatTime) {
		this.lastHeartBeatTime = lastHeartBeatTime;
	}
	
	
	
	

}










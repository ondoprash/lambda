package com.ondo.lambda;

import com.fasterxml.jackson.annotation.JsonProperty;



public class WearerInfo {
	
	@JsonProperty("wearerId")
	private String wearerId;
	
	@JsonProperty("firstName")
	private String firstName;

	@JsonProperty("lastName")
	private String lastName;	

	@JsonProperty("facilityId")
	private String facilityId;
	
	@JsonProperty("bandId")
	private String bandId;

	@JsonProperty("wearerGroupId")
	private String wearerGroupId;

	@JsonProperty("alertThresholdId")
	private Float alertThresholdId;

	@JsonProperty("alertSpike")
	private String alertSpike;

	@JsonProperty("localId")
	private String localId;
	
	@JsonProperty("baseLine")
	private String baseLine;
	
	@JsonProperty("createdDate")
	private String createdDate;

	@JsonProperty("pNEligible")
	private Integer pNEligible;

	@JsonProperty("pNSentTime")
	private Long pNSentTime;
	
	@JsonProperty("lastWarningTempTime")
	private Long lastWarningTempTime;
	
	@JsonProperty("lastWarningTemp")
	private Float lastWarningTemp;
	
	@JsonProperty("lastTempTime")
	private Long lastTempTime;
	
	@JsonProperty("lastTemp")
	private Float lastTemp;

	public String getWearerId() {
		return wearerId;
	}

	public void setWearerId(String wearerId) {
		this.wearerId = wearerId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public String getBandId() {
		return bandId;
	}

	public void setBandId(String bandId) {
		this.bandId = bandId;
	}

	public String getWearerGroupId() {
		return wearerGroupId;
	}

	public void setWearerGroupId(String wearerGroupId) {
		this.wearerGroupId = wearerGroupId;
	}

	public Float getAlertThresholdId() {
		return alertThresholdId;
	}

	public void setAlertThresholdId(Float alertThresholdId) {
		this.alertThresholdId = alertThresholdId;
	}

	public String getAlertSpike() {
		return alertSpike;
	}

	public void setAlertSpike(String alertSpike) {
		this.alertSpike = alertSpike;
	}

	public String getLocalId() {
		return localId;
	}

	public void setLocalId(String localId) {
		this.localId = localId;
	}

	public String getBaseLine() {
		return baseLine;
	}

	public void setBaseLine(String baseLine) {
		this.baseLine = baseLine;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public Integer getpNEligible() {
		return pNEligible;
	}

	public void setpNEligible(Integer pNEligible) {
		this.pNEligible = pNEligible;
	}

	public Long getpNSentTime() {
		return pNSentTime;
	}

	public void setpNSentTime(Long pNSentTime) {
		this.pNSentTime = pNSentTime;
	}

	public Long getLastWarningTempTime() {
		return lastWarningTempTime;
	}

	public void setLastWarningTempTime(Long lastWarningTempTime) {
		this.lastWarningTempTime = lastWarningTempTime;
	}

	public Float getLastWarningTemp() {
		return lastWarningTemp;
	}

	public void setLastWarningTemp(Float lastWarningTemp) {
		this.lastWarningTemp = lastWarningTemp;
	}

	public Long getLastTempTime() {
		return lastTempTime;
	}

	public void setLastTempTime(Long lastTempTime) {
		this.lastTempTime = lastTempTime;
	}

	public Float getLastTemp() {
		return lastTemp;
	}

	public void setLastTemp(Float lastTemp) {
		this.lastTemp = lastTemp;
	}
	
	
	

}










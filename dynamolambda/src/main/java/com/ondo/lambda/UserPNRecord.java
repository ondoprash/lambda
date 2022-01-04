package com.ondo.lambda;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;




public class UserPNRecord {
	@JsonProperty("userId")
	private String userId;

	@JsonProperty("facilityId")
	private String facilityId;
	
	@JsonProperty("pnRecord")
	
	private List<PnData> pnRecord;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFacilityId() {
		return facilityId;
	}

	public void setFacilityId(String facilityId) {
		this.facilityId = facilityId;
	}

	public List<PnData> getPnRecord() {
		return pnRecord;
	}

	public void setPnRecord(List<PnData> pnRecord) {
		this.pnRecord = pnRecord;
	}
	
	
	
	
}





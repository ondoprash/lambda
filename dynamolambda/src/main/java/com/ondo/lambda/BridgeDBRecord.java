package com.ondo.lambda;

import org.apache.commons.beanutils.BeanUtils;

/**
 * This DBRecord maps to the dashboard table in the DynamoDB
 */

public class BridgeDBRecord {
	/**
	 * This is Primary Key of the table
	 */
	private String id;

	private String bridgeId;
	private String facilityId;
	private Long currentTime;
	private Long DDBTime;

	private Long ttl;

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private String id;
 		private String bridgeId;
		private String facilityId;
		private Long currentTime;
		private Long DDBTime;

		private Long ttl;

		private Builder() {
		}

		public String getId() {
			return id;
		}

		public Builder setId(String id) {
			this.id = id;
			return this;
		}

		public String getBridgeId() {
			return bridgeId;
		}

		public Builder setBridgeId(String bridgeId) {
			this.bridgeId = bridgeId;
			return this;
		}

		public String getFacilityId() {
			return facilityId;
		}

		public Builder setFacilityId(String facilityId) {
			this.facilityId = facilityId;
			return this;
		}

		public Long getCurrentTime() {
			return currentTime;
		}

		public Builder setCurrentTime(Long currentTime) {
			this.currentTime = currentTime;
			return this;
		}

		public Long getDDBTime() {
			return DDBTime;
		}

		public Builder setDDBTime(Long dDBTime) {
			DDBTime = dDBTime;
			return this;
		}

		public Long getTtl() {
			return ttl;
		}

		public void setTtl(Long ttl) {
			this.ttl = ttl;
		}

		public BridgeDBRecord build() throws Exception {
			BridgeDBRecord bridgeDBRecord = new BridgeDBRecord();
			
			BeanUtils.copyProperties(bridgeDBRecord, this);
			
			return bridgeDBRecord;
		}

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

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

	public Long getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(Long currentTime) {
		this.currentTime = currentTime;
	}

	public Long getDDBTime() {
		return DDBTime;
	}

	public void setDDBTime(Long dDBTime) {
		DDBTime = dDBTime;
	}

	public Long getTtl() {
		return ttl;
	}

	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}

}

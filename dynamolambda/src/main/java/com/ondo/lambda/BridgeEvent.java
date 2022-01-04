package com.ondo.lambda;

public class BridgeEvent {
	/**
	 * $HBRP,EC62B75C3898,EC62B75C3898,-127,00000000,1607591569
	 */
	private String rawData;
	private String macId; //
	private long heartBeatTime;

	public String getRawData() {
		return rawData;
	}

	public void setRawData(String rawData) {
		this.rawData = rawData;
	}

	public String getMacId() {
		return macId;
	}

	public void setMacId(String macId) {
		this.macId = macId;
	}

	public long getHeartBeatTime() {
		return heartBeatTime;
	}

	public void setHeartBeatTime(long heartBeatTime) {
		this.heartBeatTime = heartBeatTime;
	}

}

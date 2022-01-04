package com.ondo.utils.pn;

import java.io.Serializable;

public class PushNotification implements Serializable {

	private static final long serialVersionUID = 1L;
	private String pushMsgContent;
	private String pushRegId;
	private String pushDeviceType;

	public String getPushMsgContent() {
		return pushMsgContent;
	}

	public void setPushMsgContent(String pushMsgContent) {
		this.pushMsgContent = pushMsgContent;
	}

	public String getPushRegId() {
		return pushRegId;
	}

	public void setPushRegId(String pushRegId) {
		this.pushRegId = pushRegId;
	}

	public String getPushDeviceType() {
		return pushDeviceType;
	}

	public void setPushDeviceType(String pushDeviceType) {
		this.pushDeviceType = pushDeviceType;
	}

}

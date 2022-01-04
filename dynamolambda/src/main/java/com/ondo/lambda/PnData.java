package com.ondo.lambda;

import com.fasterxml.jackson.annotation.JsonProperty;




public class PnData {
	@JsonProperty("platform")
	private String platform;

	@JsonProperty("PNRegnToken")
	private String PNRegnToken;

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getPNRegnToken() {
		return PNRegnToken;
	}

	public void setPNRegnToken(String pNRegnToken) {
		PNRegnToken = pNRegnToken;
	}
	
	
	
}

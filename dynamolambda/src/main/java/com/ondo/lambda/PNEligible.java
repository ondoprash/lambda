package com.ondo.lambda;

import org.apache.commons.beanutils.BeanUtils;

public class PNEligible {
	public static class Builder {

		private String fcltyId;

		private String wearerId;

		private Float warningTemp;

		private Float skinTemp;

		public String getFcltyId() {
			return fcltyId;
		}

		public Builder setFcltyId(String fcltyId) {
			this.fcltyId = fcltyId;
			return this;
		}

		public String getWearerId() {
			return wearerId;
		}

		public Builder setWearerId(String wearerId) {
			this.wearerId = wearerId;
			return this;
		}

		public Float getWarningTemp() {
			return warningTemp;
		}

		public Builder setWarningTemp(Float warningTemp) {
			this.warningTemp = warningTemp;
			return this;
		}

		public Float getSkinTemp() {
			return skinTemp;
		}

		public Builder setSkinTemp(Float skinTemp) {
			this.skinTemp = skinTemp;
			return this;
		}

		public PNEligible build() throws Exception  {
			 
				PNEligible pnEligible = new PNEligible();
				BeanUtils.copyProperties(pnEligible, this);
				return pnEligible;
			
		}

	}

	public static Builder builder() {

		return new Builder();

	}



	private String fcltyId;

	private String wearerId;

	private Float warningTemp;

	private Float skinTemp;

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PNEligible other = (PNEligible) obj;
		if (fcltyId == null) {
			if (other.fcltyId != null)
				return false;
		} else if (!fcltyId.equals(other.fcltyId))
			return false;
		if (wearerId == null) {
			if (other.wearerId != null)
				return false;
		} else if (!wearerId.equals(other.wearerId))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fcltyId == null) ? 0 : fcltyId.hashCode());
		result = prime * result + ((wearerId == null) ? 0 : wearerId.hashCode());
		return result;
	}

	public String getFcltyId() {
		return fcltyId;
	}

	public void setFcltyId(String fcltyId) {
		this.fcltyId = fcltyId;
	}

	public String getWearerId() {
		return wearerId;
	}

	public void setWearerId(String wearerId) {
		this.wearerId = wearerId;
	}

	public Float getWarningTemp() {
		return warningTemp;
	}

	public void setWarningTemp(Float warningTemp) {
		this.warningTemp = warningTemp;
	}

	public Float getSkinTemp() {
		return skinTemp;
	}

	public void setSkinTemp(Float skinTemp) {
		this.skinTemp = skinTemp;
	}

}

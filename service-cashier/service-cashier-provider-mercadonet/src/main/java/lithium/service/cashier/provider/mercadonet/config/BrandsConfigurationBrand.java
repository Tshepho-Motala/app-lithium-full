package lithium.service.cashier.provider.mercadonet.config;


public class BrandsConfigurationBrand {
	
	/**
	 * BetSoft base URL for this endpoint per brand id
	 */
	private String baseUrl = "";
	
	/**
	 * Shared password with Betsoft to calculate the hash parameter on requests per brand
	 */
	private String hashPassword = "";

	/**
	 * Bank id per brand
	 */
	private String bankId = "";

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getHashPassword() {
		return hashPassword;
	}

	public void setHashPassword(String hashPassword) {
		this.hashPassword = hashPassword;
	}

	public String getBankId() {
		return bankId;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	
	
}

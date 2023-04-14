package lithium.service.cashier.provider.mercadonet.data;

public class MnetResponseText extends MnetResponse {

	String responseString;
	
	public String getResponseString() {
		return responseString;
	}

	public void setResponseString(String responseString) {
		this.responseString = responseString;
	}

	@Override
	public String toString() {
		return responseString;
	}
}
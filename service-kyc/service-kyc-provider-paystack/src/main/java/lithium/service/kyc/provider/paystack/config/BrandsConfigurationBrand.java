package lithium.service.kyc.provider.paystack.config;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BrandsConfigurationBrand {
	private String platformUrl = "";
	private String apiKey = "";
	private String bvnMethod;
	private Integer bvnLength;
	private Integer connectTimeout = 60000;
	private Integer connectionRequestTimeout = 60000;
	private Integer socketTimeout = 60000;
}

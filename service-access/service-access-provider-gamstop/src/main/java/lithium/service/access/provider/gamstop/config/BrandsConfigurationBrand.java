package lithium.service.access.provider.gamstop.config;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BrandsConfigurationBrand {
	private String platformUrl = "";
	private String apiKey = "";
	private String batchPlatformUrl = "";
	private Integer connectTimeout = 60000;
	private Integer connectionRequestTimeout = 60000;
	private Integer socketTimeout = 60000;
}

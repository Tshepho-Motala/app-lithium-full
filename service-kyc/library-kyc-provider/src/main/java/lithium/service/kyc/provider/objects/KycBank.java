package lithium.service.kyc.provider.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KycBank {
	private String name;
	private String code;
}

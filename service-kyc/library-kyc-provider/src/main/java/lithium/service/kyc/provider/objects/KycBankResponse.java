package lithium.service.kyc.provider.objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KycBankResponse {
	@JsonProperty(value="id_types")
	private Map<String, Map<String,List<String>>> idTypes;
	@JsonProperty(value="bank_codes")
	private List<KycBank> bankCodes;
}

package lithium.service.sms.provider.clickatell.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName(value="data")
@JsonIgnoreProperties(ignoreUnknown=true)
public class ClickatellResponse {
	@JsonProperty
	private List<ClickatellResponseData> messages;
	@JsonProperty
	private String error;
}
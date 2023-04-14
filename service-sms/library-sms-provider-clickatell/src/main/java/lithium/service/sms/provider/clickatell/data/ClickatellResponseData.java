package lithium.service.sms.provider.clickatell.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
@JsonIgnoreProperties(ignoreUnknown=true)
public class ClickatellResponseData {
	@JsonProperty
	private boolean accepted;
	@JsonProperty
	private String to;
	@JsonProperty
	private String apiMessageId;
	@JsonProperty
	private String error;
	@JsonProperty
	private String errorCode;
}
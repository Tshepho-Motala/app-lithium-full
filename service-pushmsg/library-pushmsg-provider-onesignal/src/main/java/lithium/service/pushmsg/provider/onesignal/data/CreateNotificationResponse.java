package lithium.service.pushmsg.provider.onesignal.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public class CreateNotificationResponse {
	@JsonProperty(value="id")
	private String id;
	@JsonProperty(value="external_id")
	private String externalId;
	@JsonProperty(value="recipients")
	private Integer recipients;
	@JsonProperty(value="errors")
	private List<String> errors;
}
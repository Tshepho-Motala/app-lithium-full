package lithium.service.sms.provider.clickatell.data;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonRootName(value="request")
public class ClickatellRequest {
	@JsonProperty
	private String clientMessageId;
	@JsonProperty
	private String content;
	@JsonProperty
	private List<String> to;
//	@JsonProperty
//	private Integer userPriorityQueue;
}
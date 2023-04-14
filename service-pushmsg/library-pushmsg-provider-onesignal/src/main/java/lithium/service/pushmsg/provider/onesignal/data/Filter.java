package lithium.service.pushmsg.provider.onesignal.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lithium.service.pushmsg.provider.onesignal.data.enums.FilterField;
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
public class Filter {
	@JsonProperty
	private FilterField field;
	private String key;
	private String relation;
	private String value;
	private String operator;
}

package lithium.service.cashier.processor.neteller.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class Error {
	private String id;
	private String code;
	private String message;
	private String riskReasonCode;
	private List<String> details;
	private List<FieldError> fieldErrors;
	private List<Link> link;
	private Boolean liveMode;
}

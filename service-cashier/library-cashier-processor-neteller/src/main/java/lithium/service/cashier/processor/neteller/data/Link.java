package lithium.service.cashier.processor.neteller.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lithium.service.cashier.processor.neteller.data.enums.Rel;
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
@JsonIgnoreProperties(ignoreUnknown=true)
public class Link {
	private String rel;
	private String href;
	private String method;
}

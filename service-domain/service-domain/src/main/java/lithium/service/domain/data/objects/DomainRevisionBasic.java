package lithium.service.domain.data.objects;

import java.util.List;

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
public class DomainRevisionBasic {
	private String domainName;
	private List<DomainRevisionLabelValueBasic> labelValues;
}

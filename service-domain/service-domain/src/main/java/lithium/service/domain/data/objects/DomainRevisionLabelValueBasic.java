package lithium.service.domain.data.objects;

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
public class DomainRevisionLabelValueBasic {
	private String label;
	private String value;
	private boolean viewable;
}

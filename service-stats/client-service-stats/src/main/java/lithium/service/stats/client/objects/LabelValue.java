package lithium.service.stats.client.objects;

import java.io.Serializable;

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
public class LabelValue implements Serializable {
	private static final long serialVersionUID = 8386894365473898644L;
	private Long id;
	private String value;
	private Label label;
}
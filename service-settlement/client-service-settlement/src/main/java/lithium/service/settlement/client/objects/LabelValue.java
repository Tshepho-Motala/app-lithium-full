package lithium.service.settlement.client.objects;

import java.io.Serializable;

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
public class LabelValue implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private long id;
	int version;
	private String value;
	private Label label;
}

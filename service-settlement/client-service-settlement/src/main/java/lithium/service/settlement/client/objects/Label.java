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
public class Label implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	int version;
	private String name;
}

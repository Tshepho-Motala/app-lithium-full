package lithium.service.casino.client.objects;

import lithium.service.casino.client.data.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameSupplier implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long id;
	private int version;
	private Domain domain;
	private String name;
	private Boolean deleted;
}

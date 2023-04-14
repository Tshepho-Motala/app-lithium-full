package lithium.service.limit.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Restriction implements Serializable {
	private static final long serialVersionUID = -1;
	private Long id;
	private int version;
	private String code;
	private String name;
}

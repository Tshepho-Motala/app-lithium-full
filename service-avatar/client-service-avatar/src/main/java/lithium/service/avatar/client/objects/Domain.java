package lithium.service.avatar.client.objects;

import java.io.Serializable;

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
public class Domain implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private int version;
	private String name;
}

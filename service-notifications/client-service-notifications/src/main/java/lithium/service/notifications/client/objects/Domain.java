package lithium.service.notifications.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Domain implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private int version;
	private String name;
}

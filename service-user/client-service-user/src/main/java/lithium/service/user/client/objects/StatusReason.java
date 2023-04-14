package lithium.service.user.client.objects;

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
public class StatusReason implements Serializable {
	private Long id;
	private int version;
	private String name;
	private String description;
}

package lithium.service.entity.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdate {
	private Long statusId;
	private Long entityId;
	private String statusName;
	private String comment;
}
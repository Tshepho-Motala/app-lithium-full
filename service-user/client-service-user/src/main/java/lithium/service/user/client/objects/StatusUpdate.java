package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdate {
	private Long userId;
	private Long statusId;
	private Long statusReasonId;
	private String statusName;
	private String comment;
}

package lithium.service.user.client.objects;

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
public class UserAccountStatusUpdate {
	private String userGuid;
	private String authorGuid;
	private String statusName;
	private String statusReasonName;
	private String comment;
	private String noteCategoryName;
	private String noteSubCategoryName;
	private int notePriority;

	private Boolean selfExcluded;
	private Boolean selfExclusionPermanent;
	private String selfExclusionCreated;
	private String selfExclusionExpiry;

	private Boolean coolingOff;
	private String coolingOffCreated;
	private String coolingOffExpiry;
}

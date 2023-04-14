package lithium.service.limit.objects;

import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserRestrictionSet {
	private Long id;
	private User user;

	private String createdOn;
	private String activeFrom;
	private String activeTo;
	private Integer subType;
	private boolean active;
	private String displayName;

	private DomainRestrictionSet set;
}

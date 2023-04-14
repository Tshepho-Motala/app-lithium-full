package lithium.service.limit.client.objects;

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
    private boolean active;

    private DomainRestrictionSet set;
}
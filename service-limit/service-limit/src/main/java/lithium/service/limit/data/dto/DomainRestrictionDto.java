package lithium.service.limit.data.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
public class DomainRestrictionDto {
    private Long id;
    private DomainRestrictionSetDto set;
    private RestrictionDto restriction;
    private boolean enabled;
    private boolean deleted;
}

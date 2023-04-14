package lithium.service.limit.client.objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class DomainRestriction {
        private Long id;
        private String name;
        @JsonBackReference("set")
        private DomainRestrictionSet set;
        private Restriction restriction;
        private boolean enabled;
        private boolean deleted;
}

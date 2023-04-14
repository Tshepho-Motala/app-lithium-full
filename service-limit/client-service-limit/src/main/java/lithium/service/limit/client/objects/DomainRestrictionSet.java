package lithium.service.limit.client.objects;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@ToString
public class DomainRestrictionSet {
    private Long id;
    private int version;
    private Domain domain;
    private String name;
    private boolean systemRestriction = false;
    private boolean enabled = true;
    private boolean dwhVisible = false;
    private boolean deleted = false;
    private List<DomainRestriction> restrictions;

    private String errorMessage;

    public String errorType() {
        if (this.systemRestriction) {
            return "ERROR_DICTIONARY.SYSTEM_RESTRICTION";
        }
        return "ERROR_DICTIONARY.NORMAL_RESTRICTION";
    }
}

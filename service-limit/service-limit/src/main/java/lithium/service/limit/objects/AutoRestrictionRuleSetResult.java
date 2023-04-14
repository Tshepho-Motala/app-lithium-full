package lithium.service.limit.objects;

import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.user.client.objects.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.joda.time.DateTime;

import java.text.SimpleDateFormat;

@Data
@Builder
@AllArgsConstructor
public class AutoRestrictionRuleSetResult {
    private StringBuilder trace;
    private User user;
    private boolean result;
    private DateTime createdOn;
    private DateTime activeFrom;
    private DateTime activeTo;
    private DomainRestrictionSet restrictionSet;
    private boolean rootOnly;
    private boolean allEcosystem;
    private lithium.service.limit.enums.AutoRestrictionRuleSetOutcome outcome;

    public String getCreatedDateDisplay() {
        if (createdOn != null) return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(createdOn.toDate());
        return "";
    }

    public String getactiveFromDisplay() {
        if (activeFrom != null) return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(activeFrom.toDate());
        return "";
    }

    public String getactiveToDisplay() {
        if (activeTo != null) return new SimpleDateFormat("MMM dd, yyyy HH:mm").format(activeTo.toDate());
        return "";
    }
}

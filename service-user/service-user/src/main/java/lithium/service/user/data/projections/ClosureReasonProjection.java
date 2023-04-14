package lithium.service.user.data.projections;

import lithium.service.user.data.entities.ClosureReason;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "closureReasonProjection", types = {ClosureReason.class})
public interface ClosureReasonProjection {
    Long getId();
    String getText();
}

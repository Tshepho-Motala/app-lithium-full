package lithium.service.user.data.entities;

import java.util.Set;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "groupCategorized", types = {Group.class})
public interface GroupCategorizedProjection {

  Long getId();

  String getName();

  DomainProjection getDomain();

  Set<GRDProjection> getGrds();
}

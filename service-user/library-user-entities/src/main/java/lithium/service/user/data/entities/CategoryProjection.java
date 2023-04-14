package lithium.service.user.data.entities;

import java.util.Set;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "simpleCategory", types = {Category.class})
public interface CategoryProjection {

  Long getId();

  String getName();

  Set<RoleProjection> getRoles();
}

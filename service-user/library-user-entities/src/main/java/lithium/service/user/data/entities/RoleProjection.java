package lithium.service.user.data.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "simpleRole", types = {Role.class})
public interface RoleProjection {

  Long getId();

  String getRole();
}

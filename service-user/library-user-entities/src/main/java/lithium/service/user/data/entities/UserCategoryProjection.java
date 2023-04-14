package lithium.service.user.data.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "userCategoryProjection", types = {UserCategory.class})
public interface UserCategoryProjection {

  Long getId();

  Domain getDomain();

  String getName();

  String getDescription();
  Boolean getDwhVisible();
}

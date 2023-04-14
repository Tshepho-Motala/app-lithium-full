package lithium.service.user.data.entities;

import java.util.Date;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "simpleUserEvent", types = UserEvent.class)
public interface UserEventProjection {

  Long getId();

  String getType();

  String getMessage();

  String getData();

  Date getCreatedOn();
}

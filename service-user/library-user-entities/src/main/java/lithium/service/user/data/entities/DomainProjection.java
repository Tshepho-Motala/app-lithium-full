package lithium.service.user.data.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "simpleDomain", types = {Domain.class})
public interface DomainProjection {

  Long getId();

  String getName();
}

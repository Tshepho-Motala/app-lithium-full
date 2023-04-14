package lithium.service.user.data.entities;

import java.util.Set;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "simpleGRD", types = {GRD.class})
public interface GRDProjection {

  Set<CategoryProjection> getCategory();
}

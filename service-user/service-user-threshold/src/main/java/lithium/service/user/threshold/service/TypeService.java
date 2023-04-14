package lithium.service.user.threshold.service;

import lithium.service.user.threshold.client.enums.EType;
import lithium.service.user.threshold.data.entities.Type;

public interface TypeService {

  Type findOrCreate(EType type);
}

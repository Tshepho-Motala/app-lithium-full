package lithium.service.user.provider.threshold.services;

import lithium.service.user.provider.threshold.data.entities.Type;

public interface TypeService {

  Type findOrCreate(lithium.service.user.provider.threshold.data.enums.Type type);
}

package lithium.service.user.threshold.service.impl;

import lithium.service.user.threshold.client.enums.EType;
import lithium.service.user.threshold.data.entities.Type;
import lithium.service.user.threshold.data.repositories.TypeRepository;
import lithium.service.user.threshold.service.TypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TypeServiceImpl implements TypeService {

  @Autowired
  private TypeRepository typeRepository;

  @Override
  public Type findOrCreate(EType type) {
    return typeRepository.findOrCreateByName(type.name(), Type::new);
  }
}

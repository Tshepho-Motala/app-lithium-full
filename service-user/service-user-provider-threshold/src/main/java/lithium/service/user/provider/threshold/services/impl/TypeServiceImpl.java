package lithium.service.user.provider.threshold.services.impl;

import lithium.service.user.provider.threshold.data.entities.Type;
import lithium.service.user.provider.threshold.data.repositories.TypeRepository;
import lithium.service.user.provider.threshold.services.TypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TypeServiceImpl implements TypeService {

  @Autowired
  private TypeRepository typeRepository;

  @Override
  public Type findOrCreate(lithium.service.user.provider.threshold.data.enums.Type type) {
    return typeRepository.findOrCreateByName(type.typeName(), () -> Type.builder().id(type.id()).name(type.typeName()).build());
  }
}

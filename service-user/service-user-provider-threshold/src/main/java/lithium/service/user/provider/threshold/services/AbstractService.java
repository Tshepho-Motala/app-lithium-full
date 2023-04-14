package lithium.service.user.provider.threshold.services;

import java.util.Optional;

public interface AbstractService<T> {

  T save(T t);

   Iterable<T> findAll();

   Optional<T> findOne(Long id);

}

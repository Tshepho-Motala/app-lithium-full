package lithium.service.casino.provider.slotapi.services;

import lithium.service.casino.provider.slotapi.storage.entities.BetResultKind;
import lithium.service.casino.provider.slotapi.storage.repositories.BetResultKindRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BetResultKindService {
  @Autowired private BetResultKindRepository repository;

  public Iterable<BetResultKind> findAll() {
    return repository.findAll();
  }
}

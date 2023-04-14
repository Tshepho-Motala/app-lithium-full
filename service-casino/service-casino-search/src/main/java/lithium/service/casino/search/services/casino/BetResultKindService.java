package lithium.service.casino.search.services.casino;

import lithium.service.casino.data.entities.BetResultKind;
import lithium.service.casino.search.data.repositories.casino.BetResultKindRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("casino.BetResultKindService")
public class BetResultKindService {
  @Autowired @Qualifier("casino.BetResultKindRepository")
  private BetResultKindRepository repository;

  public Iterable<BetResultKind> findAll() {
    return repository.findAll();
  }
}

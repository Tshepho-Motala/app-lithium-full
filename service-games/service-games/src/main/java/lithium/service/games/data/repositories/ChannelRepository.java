package lithium.service.games.data.repositories;

import lithium.jpa.repository.FindOrCreateByNameRepository;
import lithium.service.games.data.entities.Channel;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChannelRepository extends FindOrCreateByNameRepository<Channel, Long> {

    List<Channel> findAll();
}
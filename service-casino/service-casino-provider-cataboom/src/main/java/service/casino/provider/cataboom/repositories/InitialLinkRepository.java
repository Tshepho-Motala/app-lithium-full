package service.casino.provider.cataboom.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Component;
import service.casino.provider.cataboom.entities.InitialLink;
import service.casino.provider.cataboom.entities.User;


@Component
public interface InitialLinkRepository extends PagingAndSortingRepository<InitialLink, Long> {
	List<InitialLink> findByUserId(Long id);
	InitialLink findByPlayid(String playid);
}
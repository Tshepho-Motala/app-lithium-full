package lithium.service.report.games.data.repositories;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;

import lithium.service.report.games.data.entities.StringValue;

public interface StringValueRepository extends PagingAndSortingRepository<StringValue, Long> {
	
	StringValue findByValue(String value);
	List<StringValue> findByUsers(long users);

}

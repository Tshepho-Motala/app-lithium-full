package lithium.service.affiliate.data.repositories;

import java.util.List;

import lithium.service.affiliate.data.entities.StringValue;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface StringValueRepository extends PagingAndSortingRepository<StringValue, Long> {
	
	StringValue findByValue(String value);
	List<StringValue> findByUsers(long users);

}

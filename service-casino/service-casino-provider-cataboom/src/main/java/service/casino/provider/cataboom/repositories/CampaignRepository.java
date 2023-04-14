package service.casino.provider.cataboom.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import org.springframework.stereotype.Component;
import service.casino.provider.cataboom.entities.Campaign;

@Component
public interface CampaignRepository extends PagingAndSortingRepository<Campaign, Long>, JpaSpecificationExecutor<Campaign> {
	Campaign findByDomainNameAndToken(String domain,String token);
	List<Campaign> findByDomainName(String domainName);
	Campaign findByToken(String token);
	Campaign findByCampaignName(String campaignid);
	Campaign findByTokenAndCampaignName(String token, String campaignid);

	default Campaign findOne(Long id) {
		return findById(id).orElse(null);
	}
}

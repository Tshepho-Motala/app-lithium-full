package lithium.service.casino.cms.storage.repositories;

import lithium.service.casino.cms.storage.entities.Banner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface BannerRepository extends PagingAndSortingRepository<Banner, Long> {

    List<Banner> findAllByDomainNameAndDeletedFalse(String domainName);

    Page<Banner> findAllByBannerSchedulesClosedFalseAndBannerSchedulesEndDateAfter(Date date, Pageable page);

}

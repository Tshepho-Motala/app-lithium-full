package lithium.service.casino.cms.storage.repositories;

import lithium.service.casino.cms.storage.entities.BannerSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface BannerScheduleRepository extends PagingAndSortingRepository<BannerSchedule, Long> {

    @Query("SELECT bannerSchedule "
            + " FROM BannerSchedule bannerSchedule "
            + " WHERE bannerSchedule.closed = false "
            + " AND bannerSchedule.deleted = false "
            + " AND bannerSchedule.endDate < CURRENT_DATE " )
    Page<BannerSchedule> findAllExpired(Pageable pageable);

    List<BannerSchedule> findAllByBannerIdAndClosedFalseAndDeletedFalse(Long bannerId);
}

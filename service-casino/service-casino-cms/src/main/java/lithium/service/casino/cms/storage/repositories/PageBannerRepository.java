package lithium.service.casino.cms.storage.repositories;

import lithium.service.casino.cms.storage.entities.PageBanner;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PageBannerRepository extends PagingAndSortingRepository<PageBanner, Long>, JpaSpecificationExecutor<PageBanner> {

    @Query("SELECT pageBanner "
            + " FROM PageBanner pageBanner "
            + " INNER JOIN pageBanner.lobby lobby "
            + " INNER JOIN pageBanner.banner banner "
            + " INNER JOIN banner.bannerSchedules bannerSchedules "
            + " WHERE pageBanner.deleted = false"
            + " AND pageBanner.primaryNavCode = :primaryNavCode "
            + " AND pageBanner.secondaryNavCode = :secondaryNavCode "
            + " AND pageBanner.channel = :channel"
            + " AND lobby.id = :lobbyId "
            + " AND banner.deleted = false "
            + " AND (banner.timeFrom is null OR banner.timeFrom <= CURRENT_TIME) "
            + " AND (banner.timeTo is null OR banner.timeTo >= CURRENT_TIME) "
            + " AND (banner.loggedIn is null OR banner.loggedIn = :loggedIn) "
            + " AND bannerSchedules.closed = false "
            + " AND bannerSchedules.deleted = false "
            + " AND bannerSchedules.startDate <= CURRENT_DATE "
            + " AND bannerSchedules.endDate >= CURRENT_DATE ")
    List<PageBanner> findAllVisibleBanners(@Param("primaryNavCode") String primaryNavCode,
                                           @Param("secondaryNavCode") String secondaryNavCode,
                                           @Param("channel") String channel,
                                           @Param("lobbyId") Long lobbyId,
                                           @Param("loggedIn") Boolean loggedIn);

    List<PageBanner> findAllByIdIn(List<Long> ids);

    List<PageBanner> findAllByDeletedFalseAndBannerDeletedFalseAndBannerDomainNameAndPrimaryNavCodeAndSecondaryNavCodeAndChannel(String domainName, String primaryNavCode, String secondaryNavCode, String channel);

    PageBanner findByBannerDomainNameAndPrimaryNavCodeAndSecondaryNavCodeAndChannelAndLobbyIdAndBannerId(String domainName,
                                                                                                         String primaryNavCode,
                                                                                                         String secondaryNavCode,
                                                                                                         String channel,
                                                                                                         Long lobbyId,
                                                                                                         Long bannerId);
}

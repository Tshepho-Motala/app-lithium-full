package lithium.service.user.data.repositories;


import java.time.LocalDateTime;
import java.util.List;
import lithium.service.client.objects.Granularity;
import lithium.service.user.data.entities.playtimelimit.LimitType;
import lithium.service.user.data.entities.playtimelimit.PlayerPlayTimeLimit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerTimeLimitRepository extends PagingAndSortingRepository<PlayerPlayTimeLimit, Long> {

  List<PlayerPlayTimeLimit> findByUserId(Long userId);

  PlayerPlayTimeLimit findByUserIdAndType(Long userId, LimitType type);

  Page<PlayerPlayTimeLimit> findByType(LimitType type, Pageable pageRequest);

  List<PlayerPlayTimeLimit> findTop1000ByDomainNameAndGranularityAndTypeAndTimeInMinutesUsedGreaterThanAndLastResetNotOrderByIdAsc(String domainName,
      Granularity granularity, LimitType type, long timeInMinutesUsedGreaterThan, LocalDateTime now);

//  @Modifying
//  @Transactional
//  @Query("UPDATE #{#entityName} t "
//      + " SET t.timeInMinutesUsed = 0 "
//      + " WHERE t.domainName = :domainName "
//      + " AND t.granularity = :granularity "
//      + " AND t.timeInMinutesUsed > 0 "
//      + " AND t.type = :type ")
//  void resetTimeUsedCounter(@Param("domainName") String domainName, @Param("granularity") Granularity granularity, @Param("type") LimitType type);
  default PlayerPlayTimeLimit findOne(Long id) {
    return findById(id).orElse(null);
  }

  Page<PlayerPlayTimeLimit> findAllByIdGreaterThan(Pageable pageable, long lastIdProcessed);
}

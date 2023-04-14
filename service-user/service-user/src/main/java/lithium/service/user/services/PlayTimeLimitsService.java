package lithium.service.user.services;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.PlayerLimitsClient;
import lithium.service.limit.client.exceptions.Status477DomainTimeSlotLimitDisabledException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.objects.PlayerTimeSlotLimit;
import lithium.service.user.data.entities.playtimelimit.PlayerPlayTimeLimit;
import lithium.service.user.data.repositories.PlayerTimeLimitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
public class PlayTimeLimitsService {

  @Autowired
  private CachingDomainClientService cachingDomainClientService;
  @Autowired
  private PlayerTimeLimitRepository repository;

  @Autowired
  private LithiumServiceClientFactory services;


  private PlayerLimitsClient getPlayerLimitsClient() {
    PlayerLimitsClient cl = null;
    try {
      cl = services.target(PlayerLimitsClient.class, "service-limit", true);
    } catch (LithiumServiceClientFactoryException e) {
      log.error("Problem getting player limits client", e);
    }
    return cl;
  }

  public static boolean isTimeFormatValid(String value) {
    try {
      String[] time = value.split(":");
      return Integer.parseInt(time[0]) < 24 && Integer.parseInt(time[1].substring(0,2)) < 60;
    } catch (Exception e) {
      return false;
    }
  }

  public long getTimestamp(String timestamp) {
    // Timestamp = 19:00
    String[] parts = timestamp.split(":");       // 19 , 00

    String hourString = parts[0];                      // 19
    String minuteString = parts[1].substring(0,2);     // 00

    int hour = Integer.parseInt(hourString);
    int minute = Integer.parseInt(minuteString);

    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);

    long utcTimestamp = calendar.getTimeInMillis();
    return utcTimestamp;
  }

  public boolean isPlayerTimeFrameLimitsActivatedForDomain(String domainName) {
    try {
      Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
      if (domain == null){
        return false;
      }
      Boolean domainHasTimeFrameLimitsActivated = domain.getPlayerTimeSlotLimits();
      if (domainHasTimeFrameLimitsActivated == true) {
        return true;
      }
      return false;
    } catch(Status550ServiceDomainClientException e) {
      //We don't want to throw an exception if the domain isn't found, as only configured domains will throw the 478 error,
      //non-configured domains must bypass the check
      return false;
    }
  }

  //We evaluate that the 'fromTime' is not equal 'toTime' (must all be be within a 24h period '00:00 - 23:59')
  public boolean isTimeSlotValid(Date fromDate, Date toDate) {
    Calendar startSHCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    startSHCalendar.setTime(fromDate);
    Calendar stopSHCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    stopSHCalendar.setTime(toDate);

    int startSHhour = startSHCalendar.get(Calendar.HOUR_OF_DAY);
    int startSHmin = startSHCalendar.get(Calendar.MINUTE);
    int fromUTCTime = startSHhour*60 + startSHmin;  //this

    int stopSHhour = stopSHCalendar.get(Calendar.HOUR_OF_DAY);
    int stopSHmin = stopSHCalendar.get(Calendar.MINUTE);
    int toUTCTime = stopSHhour*60 + stopSHmin;  //this

    Date elapsedTime = calculateTimeFrameLimitsDuration(fromDate, toDate);
    if (elapsedTime.getHours() >= 24  || fromUTCTime == toUTCTime) {
      return false;
    } else {
      return true;
    }
  }

  private static Date calculateTimeFrameLimitsDuration(Date start, Date stop)
  {
    Date diff = new Date(0, 0, 0);

    diff.setDate(start.getDate() + 1);
    diff.setMonth(start.getMonth());
    diff.setYear(start.getYear());
    diff.setSeconds(stop.getSeconds() - start.getSeconds());
    diff.setMinutes(stop.getMinutes() - start.getMinutes());
    diff.setHours(stop.getHours() - start.getHours());

    return(diff);
  }

  public void createPlayerTimeSlotLimitForUser(String playerGuid, Long playerId, String domainName, String timeSlotLimitStart, String timeSlotLimitEnd)
      throws Status478TimeSlotLimitException, Status477DomainTimeSlotLimitDisabledException {
    //Convert inputs received in format: 'HH:MM' time to long timestamps
    Long timeFromUtc = getTimestamp(timeSlotLimitStart);
    Long timeToUtc = getTimestamp(timeSlotLimitEnd);
    log.debug("Creating PlayerTimeFrameLimit/PlayerTimeSlotLimit for user: " + playerGuid + ", setting timeSlotLimitStart to: " + timeSlotLimitStart + " & setting timeSlotLimitEnd to: " + timeSlotLimitEnd + ".");
    Response<PlayerTimeSlotLimit> createdPlayerTimeSlotLimit = getPlayerLimitsClient().setPlayerTimeSlotLimit(playerGuid, playerId, domainName, timeFromUtc, timeToUtc);
    if (createdPlayerTimeSlotLimit.getData() != null && createdPlayerTimeSlotLimit.getStatus().message().equals("OK")) {
      log.debug("PlayerTimeSlotLimit/PlayerTimeFrameLimit created successfully for user: " + playerGuid + ", timeSlotLimitStart set to: " + timeSlotLimitStart + " & timeSlotLimitEnd set to: " + timeSlotLimitEnd + ".");
    } else {
      log.error("User registered successfully, but could not save/create timeSlotLimit/timeFrameLimit. for user: " + playerGuid);
    }
  }

  public PlayerPlayTimeLimit findById(long id) {
    return repository.findOne(id);
  }

  public boolean isDepositLimitServiceActivatedForDomain(String domainName) {
    Domain clientDomain = getClientDomain(domainName);
    if (clientDomain == null) {
      return false;
    }
    if ((!ObjectUtils.isEmpty(clientDomain.getPlayerDepositLimits()) && clientDomain.getPlayerDepositLimits())) {
      return clientDomain.getPlayerDepositLimits();
    }
    return false;
  }

  public boolean isBalanceLimitServiceActivatedForDomain(String domainName) {
    Domain clientDomain = getClientDomain(domainName);
    if (clientDomain == null) {
      return false;
    }
    if ((!ObjectUtils.isEmpty(clientDomain.getPlayerBalanceLimit()) && clientDomain.getPlayerBalanceLimit())) {
      return clientDomain.getPlayerBalanceLimit();
    }
    return false;
  }

  public boolean isPlayTimeLimitServiceActivatedForDomain(String domainName) {
    Domain clientDomain = getClientDomain(domainName);
    if (ObjectUtils.isEmpty(clientDomain)) {
      return false;
    }

    return clientDomain.isPlaytimeLimit();
  }

  private Domain getClientDomain(String domainName) {
    Domain clientDomain = null;
    try {
      clientDomain = cachingDomainClientService
          .retrieveDomainFromDomainService(domainName);
    } catch (Status550ServiceDomainClientException e) {
      log.error("cachingDomainClientService request fail with exception" + e.getMessage());
    }
    return clientDomain;
  }

}

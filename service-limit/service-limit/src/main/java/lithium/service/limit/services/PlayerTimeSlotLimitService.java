package lithium.service.limit.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status426InvalidParameterProvidedException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.exceptions.Status477DomainTimeSlotLimitDisabledException;
import lithium.service.limit.client.exceptions.Status478TimeSlotLimitException;
import lithium.service.limit.client.objects.PlayerTimeSlotLimitResponse;
import lithium.service.limit.client.objects.TimeSlotLimitRequest;
import lithium.service.limit.data.entities.PlayerTimeSlotLimit;
import lithium.service.limit.data.objects.ChangeLogTimeSlotLimit;
import lithium.service.limit.data.repositories.DomainRepository;
import lithium.service.translate.client.objects.RegistrationError;
import lithium.service.user.client.objects.User;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import lithium.service.limit.data.repositories.PlayerTimeSlotLimitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@Service
@Slf4j
public class PlayerTimeSlotLimitService {
    @Autowired
    PlayerTimeSlotLimitRepository playerTimeSlotLimitRepository;
    @Autowired
    private ChangeLogService changeLogService;
    @Autowired
    CachingDomainClientService cachingDomainClientService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private TokenStore tokenStore;

    public Boolean isBlockedByLimit(final String playerGuid, final String domainName, final String type) {
        try {
            checkLimits(playerGuid, domainName, type);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    public LithiumTokenUtil tokenUtil() {
        LithiumTokenUtil util = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        //Building LithiumTokenUtil from the spring security context
        if (authentication instanceof OAuth2Authentication ) {
            util = LithiumTokenUtil.builder(tokenStore, authentication).build();
        }

        return util;
    }

    public void checkLimits(final String playerGuid, final String domainName, final String type) throws Status478TimeSlotLimitException {
        if(!shouldTimeSlotLimitBeActioned(domainName)) {
            return;
        }

        PlayerTimeSlotLimit timeSlotLimit = playerTimeSlotLimitRepository.findByPlayerGuid(playerGuid);
        if(timeSlotLimit == null) {
            return;
        }

        Long userFrom = timeSlotLimit.getLimitFromUtc();
        Long userTo = timeSlotLimit.getLimitToUtc();

        if(userFrom == 0 || userTo == 0) {
            // Zero values are basically 'null', therefore we can skip the check
            return;
        }

        final LocalTime localTime = LocalTime.now();
        Date fromDate = new Date(userFrom);                         // 01 Jan 1700 08:00 || 05 May 2021 08:00
        Date toDate = new Date(userTo);                             // 01 Jan 1700 21:00 || 05 May 2021 21:00

        boolean timeSlotBlocked = isCurrentBetween(localTime, fromDate, toDate);
        if(timeSlotBlocked) {
            String dictionaryKey = "ERROR_DICTIONARY.MY_ACCOUNT.BLOCKED_BY_TIME_SLOT_LIMIT";
            if(type == "bet") {
                // Bet error dictionary
                dictionaryKey = "ERROR_DICTIONARY.MY_ACCOUNT.BET_BLOCKED_BY_TIME_SLOT_LIMIT";
            } else if (type == "deposit") {
                // Deposit error dictionary
                dictionaryKey = "ERROR_DICTIONARY.MY_ACCOUNT.DEPOSIT_BLOCKED_BY_TIME_SLOT_LIMIT";
            }
            throw new Status478TimeSlotLimitException(messageSource.getMessage(dictionaryKey, new Object[]{new lithium.service.translate.client.objects.Domain(playerGuid.split("/")[0])}, "[D] Current Time Slot Limit prevents this action.", LocaleContextHolder.getLocale()));
        }
    }

    /**
     * Checks to see if the current UTC time is between two UTC times.
     * This check ignores the DAY, MONTH, and YEAR - it only does a comparison on the HOUR and MINUTE.
     * @param fromDate The FROM date
     * @param toDate The TO date
     * @return true if comparisonDate is between the fromDate and toDate, false if not
     */
    private boolean isCurrentBetween(final LocalTime currentTime, Date fromDate, Date toDate) {
        Calendar startSHCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        startSHCalendar.setTime(fromDate);
        Calendar stopSHCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        stopSHCalendar.setTime(toDate);

        int startHour = startSHCalendar.get(Calendar.HOUR_OF_DAY);
        int startMin = startSHCalendar.get(Calendar.MINUTE);
        int stopHour = stopSHCalendar.get(Calendar.HOUR_OF_DAY);
        int stopMin = stopSHCalendar.get(Calendar.MINUTE);

        if(startHour > stopHour && currentTime.now().isBefore(LocalTime.of(stopHour, stopMin)) || (currentTime.now().isAfter(LocalTime.of(startHour, startMin))
                && currentTime.now().isBefore(LocalTime.of(stopHour, stopMin)))) {
            return true;
        }
        return false;
    }

    /**
     * Finds a TimeSlotLimit by a player's GUID
     * @param playerGuid The GUID of a player
     * @return PlayerTimeSlotLimit if a limit is found, null if not
     */
    public PlayerTimeSlotLimit findPlayerLimit(String playerGuid)throws Status550ServiceDomainClientException{
        PlayerTimeSlotLimit findPlayerTimeSlotLimit =  playerTimeSlotLimitRepository.findByPlayerGuid(playerGuid);
        return findPlayerTimeSlotLimit;
    }

    public static Date calculateTimeFrameLimitsDuration(Date start, Date stop)
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

    /**
     * Creates a new TimeSlotLimit for a player - all values are stored in UTC time.
     * Creates a changelog entry for this action.
     * @param playerGuid The GUID of a player
     * @param playerId The ID of (the same) player
     * @param utcFrom UTC timestamp
     * @param utcTo UTC timestamp
     * @param tokenUtil JWT token of the currently logged in user
     * @return PlayerTimeSlotLimit of the new limit created for the player
     */
    public PlayerTimeSlotLimit createPlayerLimit(String playerGuid, Long playerId, long utcFrom, long utcTo, LithiumTokenUtil tokenUtil) throws Status478TimeSlotLimitException {

        Date fromDate = new Date(utcFrom);                         // 01 Jan 1700 08:00 || 05 May 2021 08:00
        Date toDate = new Date(utcTo);

        Date elapsedTime = calculateTimeFrameLimitsDuration(fromDate, toDate);
        if (fromDate.getTime() > toDate.getTime()) {
            toDate.setDate(fromDate.getDate() + 1);
            utcTo = addOneDay(new SimpleDateFormat("HH:mm").format(toDate));
        }
        if (elapsedTime.getHours() >= 24 || utcFrom == utcTo) {
            // From and To input validation error dictionary
            String dictionaryKey = "ERROR_DICTIONARY.MY_ACCOUNT.TIME_SLOT_LIMIT_RANGE_VALIDATION";
            throw new Status478TimeSlotLimitException(messageSource.getMessage(dictionaryKey, new Object[]{new lithium.service.translate.client.objects.Domain(playerGuid.split("/")[0])}, "[D] From time must be less than To time", LocaleContextHolder.getLocale()));
        }

        PlayerTimeSlotLimit timeSlotLimitSet = playerTimeSlotLimitRepository.findByPlayerGuid(playerGuid);
        // here we Update/Modify the time slot limits only if they exist for this user;
        if (timeSlotLimitSet != null ) {
            return updatePlayerLimit(playerGuid, playerId, utcFrom, utcTo, tokenUtil);
        }
        // Build and save New time slot limits
        lithium.service.limit.data.entities.Domain domain = domainRepository.findOrCreateByName(playerGuid.split("/")[0], () -> new lithium.service.limit.data.entities.Domain());
        PlayerTimeSlotLimit timeSlotLimit = PlayerTimeSlotLimit.builder()
                .playerGuid(playerGuid)
                .limitFromUtc(utcFrom)
                .limitToUtc(utcTo)
                .domain(domain)
                .build();
        PlayerTimeSlotLimit savedTimeSlotLimit = playerTimeSlotLimitRepository.save(timeSlotLimit);
        updateChangelogForUser(savedTimeSlotLimit, "create", playerId, tokenUtil);
        return savedTimeSlotLimit;
    }

    /**
     * Updates the TimeSlotLimit for a player - all values are stored in UTC time.
     * Creates a changelog entry for this action.
     * @param playerGuid The GUID of a player
     * @param playerId The ID of (the same) player
     * @param utcFrom UTC timestamp
     * @param utcTo UTC timestamp
     * @param tokenUtil JWT token of the currently logged in user
     * @return PlayerTimeSlotLimit of the updated limit for the player
     */
    public PlayerTimeSlotLimit updatePlayerLimit(String playerGuid, Long playerId, long utcFrom, long utcTo, LithiumTokenUtil tokenUtil) {
        PlayerTimeSlotLimit timeSlotLimitSet = playerTimeSlotLimitRepository.findByPlayerGuid(playerGuid);
        PlayerTimeSlotLimit oldPlayerTimeSlotLimit = PlayerTimeSlotLimit.builder().limitFromUtc(timeSlotLimitSet.getLimitFromUtc())
                .limitToUtc(timeSlotLimitSet.getLimitToUtc()).playerGuid(timeSlotLimitSet.getPlayerGuid())
                .domain(timeSlotLimitSet.getDomain())
                .build();
        String actionType = "edit"; // this might be a technical debt, this needs to be an enum type
        timeSlotLimitSet.setLimitFromUtc(utcFrom);
        timeSlotLimitSet.setLimitToUtc(utcTo);
        updateChangelogForUser(timeSlotLimitSet, actionType, playerId, tokenUtil, oldPlayerTimeSlotLimit);

        timeSlotLimitSet = playerTimeSlotLimitRepository.save(timeSlotLimitSet);
        return timeSlotLimitSet;
    }

    /**
     * Removes a TimeSlotLimit from a player
     * @param playerGuid The GUID of a player
     * @param playerId The ID of (the same) player
     * @param tokenUtil JWT token of the currently logged in user
     */
    public void removePlayerLimit(String playerGuid, Long playerId, LithiumTokenUtil tokenUtil) {
        PlayerTimeSlotLimit playerTimeSlotLimit = playerTimeSlotLimitRepository.findByPlayerGuid(playerGuid);
        if(playerTimeSlotLimit == null) {
            return;
        }
        updateChangelogForUser(playerTimeSlotLimit, "delete", playerId, tokenUtil);
        playerTimeSlotLimitRepository.deleteByPlayerGuid(playerGuid);
    }

    /**
     * Converts a PlayerTimeSlotLimit to a ChangeLogTimeSlotLimit
     * @param playerTimeSlotLimit Valid PlayerTimeSlotLimit
     * @return ChangeLogTimeSlotLimit
     */
    public ChangeLogTimeSlotLimit convertToLocalTimeSlotLimits(PlayerTimeSlotLimit playerTimeSlotLimit) {
        String fromSlotTimeLimit = convertUtcToTimestamp(playerTimeSlotLimit.getLimitFromUtc());
        String toSlotTimeLimit = convertUtcToTimestamp(playerTimeSlotLimit.getLimitToUtc());

        ChangeLogTimeSlotLimit changeLogTimeSlotLimit = ChangeLogTimeSlotLimit.builder()
                .playerGuid(playerTimeSlotLimit.getPlayerGuid())
                .domainName(playerTimeSlotLimit.getDomain().getName())
                .limitFromUtc(fromSlotTimeLimit)
                .limitToUtc(toSlotTimeLimit)
                .build();

        return changeLogTimeSlotLimit;
    }

    /**
     * Utility method for a uniform way of updating user's changelogs
     * @param playerTimeSlotLimit
     * @param type delete / create
     * @param playerId
     * @param tokenUtil
     */
    public void updateChangelogForUser(PlayerTimeSlotLimit playerTimeSlotLimit, String type, Long playerId, LithiumTokenUtil tokenUtil) {
        try {
            ChangeLogTimeSlotLimit changeLogTimeSlotLimit = convertToLocalTimeSlotLimits(playerTimeSlotLimit);
            String[] fields = {"limitFromUtc", "limitToUtc"};
            List<ChangeLogFieldChange> clfc = changeLogService.copy(changeLogTimeSlotLimit, new ChangeLogTimeSlotLimit(), fields);

            if(Objects.equals(type, "delete")) {
                clfc = changeLogService.copy(new ChangeLogTimeSlotLimit(), changeLogTimeSlotLimit, fields);
            }
            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.limit", type, playerId, tokenUtil.guid(), tokenUtil,
                    null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.TIME_FRAMES_LIMITS, 40, playerTimeSlotLimit.getDomain().getName());
        } catch (Exception e) {
            log.error("User time slot limit created, but changelog failed. (" + playerTimeSlotLimit.getPlayerGuid().split("/")[1] + ")", e);
        }
    }

    /**
     * Utility method for a uniform way of updating user's changelogs
     * @param playerTimeSlotLimit
     * @param type edit
     * @param playerId
     * @param tokenUtil
     */
    public void updateChangelogForUser(PlayerTimeSlotLimit playerTimeSlotLimit, String type, Long playerId, LithiumTokenUtil tokenUtil, PlayerTimeSlotLimit oldPlayerTSL) {
        try {
            ChangeLogTimeSlotLimit changeLogTimeSlotLimit = convertToLocalTimeSlotLimits(playerTimeSlotLimit);
            String[] fields = {"limitFromUtc", "limitToUtc"};
            List<ChangeLogFieldChange> clfc = changeLogService.copy(changeLogTimeSlotLimit, new ChangeLogTimeSlotLimit(), fields);

            if (Objects.equals(type, "edit")) {
                ChangeLogTimeSlotLimit oldChangeLogTimeSlotLimit = convertToLocalTimeSlotLimits(oldPlayerTSL);
                clfc = changeLogService.copy(changeLogTimeSlotLimit, oldChangeLogTimeSlotLimit, fields);
            }

            changeLogService.registerChangesForNotesWithFullNameAndDomain("user.limit", type, playerId, tokenUtil.guid(), tokenUtil,
                    null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.TIME_FRAMES_LIMITS, 40, playerTimeSlotLimit.getDomain().getName());
        } catch (Exception e) {
            log.error("User time slot limit created, but changelog failed. (" + playerTimeSlotLimit.getPlayerGuid().split("/")[1] + ")", e);
        }
    }

    /**
     * Check to see if a domain is configured to allow actions on TimeSlotLimit
     * @param domainName Name of the domain to check
     * @return true if the domain is configured and allowed, false if not
     */
    public boolean shouldTimeSlotLimitBeActioned(String domainName) {
        try {
            // Fetch the current domain by domain name
            Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
            // Fetch the TimeSlotLimits property from the domain
            Boolean hasLimit = domain.getPlayerTimeSlotLimits();
            // If the property == true, the domain is configured to allow Time Slot Limit actions

            // If the property == false, the domain is configured to disallow Time Slot Limit actions
            // If the property == null , ^

            if (hasLimit == true) {
                return true;
            }
            return false;
        } catch(Status550ServiceDomainClientException e) {
            // We don't want to throw an exception if the domain isn't found, as only configured domains will throw the 478 error,
            // non-configured domains must bypass the check
            return false;
        }
    }

    /**
     * Throws an exception if the domain is not configured to allow for TimeSlotLimit actions
     * @param domainName Name of the domain to check
     * @throws Status477DomainTimeSlotLimitDisabledException
     */
    public void throwIfTimeSlotLimitIsDisabledForDomain(String domainName) throws Status477DomainTimeSlotLimitDisabledException {
        if(!shouldTimeSlotLimitBeActioned(domainName)) {
            throw new Status477DomainTimeSlotLimitDisabledException(messageSource.getMessage("ERROR_DICTIONARY.MY_ACCOUNT.DOMAIN_TIME_SLOT_LIMIT_DISABLED", new Object[]{new lithium.service.translate.client.objects.Domain(domainName)}, "[D] Domain is not configured to access Time Slot Limits.", LocaleContextHolder.getLocale()));
        }
    }

    /**
     * Converts from a string timestamp HH:MM+HH:MM with zone to a UTC long
     * @param timestamp String timestamp eg "14:35+01:00"
     * @return UTC long
     */
    public long convertFromTimestampToUtcWithZone(String timestamp) {
        // Timestamp = 14:00+1:00
        String[] parts = timestamp.split(":");       // 14 , 00+1 , 00

        String hourString = parts[0];                      // 14
        String minuteString = parts[1].substring(0,2);     // 00
        String modifier = parts[1].substring(2,3);         // +
        String zoneString = parts[1].substring(3);         // 1

        int hour = Integer.parseInt(hourString);
        int minute = Integer.parseInt(minuteString);
        int zone = Integer.parseInt(zoneString);

        if(modifier.equals("+")) {
            hour = hour - zone;
        } else if (modifier.equals("-")) {
            hour = hour + zone;
        }

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        long utcTimestamp = calendar.getTimeInMillis();
        return utcTimestamp;
    }

    /**
     * Converts from a string timestamp HH:MM without zone to a UTC long
     * @param timestamp String timestamp eg "14:35"
     * @return UTC long
     */
    public long convertFromTimestampToUtc(String timestamp) {
        String[] parts = timestamp.split(":");

        String hourString = parts[0].trim();
        String minuteString = parts[1].substring(0,2).trim();

        int hour = Integer.parseInt(hourString);
        int minute = Integer.parseInt(minuteString);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        return calendar.getTimeInMillis();
    }

    public long addOneDay(String timestamp) {
        // Timestamp = 19:00
        String[] parts = timestamp.split(":");       // 19 , 00

        String hourString = parts[0].trim();                      // 19
        String minuteString = parts[1].substring(0,2).trim();     // 00

        int hour = Integer.parseInt(hourString);
        int minute = Integer.parseInt(minuteString);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.add(Calendar.DATE, 1);

        long utcTimestamp = calendar.getTimeInMillis();
        return utcTimestamp;
    }

    /**
     * Converts a UTC long into a string timestamp
     * @param utc UTC long time
     * @return String timestamp in UTC eg "13:35"
     */
    public String convertUtcToTimestamp(Long utc) {
        Date date = new Date(utc);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.setTime(date);

        int h = calendar.get(Calendar.HOUR_OF_DAY);
        int m = calendar.get(Calendar.MINUTE);

        // This does not convert UTC to local time, the timestamp is of UTC just in string format
        return String.format("%02d:%02d", h, m);
    }

    /**
     * Converts a valid PlayerTimeSlotLimit to PlayerTimeSlotLimitResponse for front-end returns
     * @param limit Valid PlayerTimeSlotLimit
     * @return PlayerTimeSlotLimitResponse
     */
    public PlayerTimeSlotLimitResponse convertLimitToResponse(PlayerTimeSlotLimit limit) {
        PlayerTimeSlotLimitResponse response = new PlayerTimeSlotLimitResponse();

        if(limit != null) {
            response.setFromTimestampUTC(convertUtcToTimestamp(limit.getLimitFromUtc()));
            response.setToTimestampUTC(convertUtcToTimestamp(limit.getLimitToUtc()));
        }
        return response;
    }

    public void validateTimeSlotLimitInputReceived(TimeSlotLimitRequest request, User user) throws Status426InvalidParameterProvidedException {
        if (request.getFromTimeUTC() == null || request.getToTimeUTC() == null) {
            String parameter = "fromTimeUTC = null"  + " toTimeUTC = null";
            throw new Status426InvalidParameterProvidedException(RegistrationError.INVALID_PARAMETER.getResponseMessageLocal(messageSource, user.getDomain().getName(), new Object[] { parameter }));
        }
        Long utcTimeFrom = convertFromTimestampToUtc(request.getFromTimeUTC());
        Long utcTimeTo = convertFromTimestampToUtc(request.getToTimeUTC());
        Date fromDate = new Date(utcTimeFrom);
        Date toDate = new Date(utcTimeTo);
        if (!isTimeSlotValid(fromDate, toDate) || !isTimeFormatValid(request.getFromTimeUTC()) || !isTimeFormatValid(request.getToTimeUTC())) {
            String parameter = "fromTimeUTC = " + request.getFromTimeUTC() + " toTimeUTC = " + request.getToTimeUTC();
            throw new Status426InvalidParameterProvidedException(RegistrationError.INVALID_PARAMETER.getResponseMessageLocal(messageSource, user.getDomain().getName(), new Object[] { parameter }));
        }
    }

    private boolean isTimeFormatValid(String value) {
        try {
            String[] time = value.split(":");
            return Integer.parseInt(time[0].trim()) < 24 && Integer.parseInt(time[1].substring(0,2).trim()) < 60;
        } catch (Exception e) {
            return false;
        }
    }

    // Evaluate that the 'fromTimeUTC' is not equal 'toTimeUTC' (must all be be within a 24h period '00:00 - 23:59')
    private boolean isTimeSlotValid(Date fromDate, Date toDate) {
        Calendar startSHCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        startSHCalendar.setTime(fromDate);
        Calendar stopSHCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        stopSHCalendar.setTime(toDate);

        int startSHhour = startSHCalendar.get(Calendar.HOUR_OF_DAY);
        int startSHmin = startSHCalendar.get(Calendar.MINUTE);
        int fromUTCTime = startSHhour*60 + startSHmin;

        int stopSHhour = stopSHCalendar.get(Calendar.HOUR_OF_DAY);
        int stopSHmin = stopSHCalendar.get(Calendar.MINUTE);
        int toUTCTime = stopSHhour*60 + stopSHmin;

        Date elapsedTime = calculateTimeFrameLimitsDuration(fromDate, toDate);
        if (elapsedTime.getHours() >= 24  || fromUTCTime == toUTCTime) {
            return false;
        } else {
            return true;
        }
    }

}

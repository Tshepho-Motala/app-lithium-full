package lithium.service.limit.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.data.entities.RealityCheckSet;
import lithium.service.limit.data.entities.RealityCheckTrackData;
import lithium.service.limit.data.entities.RealityCheckTrackDataFE;
import lithium.service.limit.data.repositories.RealityCheckDataRepository;
import lithium.service.limit.data.repositories.RealityCheckSetRepository;
import lithium.tokens.LithiumTokenUtil;
import lithium.service.limit.enums.RealityCheckStatusType;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.User;
import lithium.service.user.client.service.UserApiInternalClientService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
@Slf4j
public class RealityCheckService {
    @Autowired
    private RealityCheckSetRepository realityCheckSetRepository;

    @Autowired
    private RealityCheckDataRepository realityCheckDataRepository;

    @Autowired
    private ChangeLogService changeLogService;

    @Autowired
    private UserApiInternalClientService userApiInternalClientService;

    @Autowired
    private ModelMapper mapper;

    @Autowired
    private CachingDomainClientService cachingDomainClientService;


    private static final String DOMAIN_SETTING_REALITY_CHECK = "default-reality-check";
    private static final long DEFAULT_REALITY_CHECK_TIME = 3600000;
    private static final String DOMAIN_SETTING_REALITY_CHECK_PERIODS_IN_MILLIS = "default-reality-check-periods-in-ms";
    private static final String DEFAULT_REALITY_CHECK_PERIODS_IN_MILLIS = "0,1800000,3600000";
    private static final String DEFAULT_REALITY_CHECK_PERIODS_IN_MINS = "0,30,60";

    /**
     * FIXME:
     *   1. RealityCheckSet.java isn't actually a "set" - Some minor refactoring would make the code more readable.
     *   2. lithium.service.limit.api.controllers.FrontendRealityCheckController#get -->
     *      lithium.service.limit.services.RealityCheckService#getOrCreateCurrentRealitySet should not save the entity
     *      but rather just construct and return the obj.
     *   3. Changelogs/notes registration throws an exception if anything goes wrong (as it should) but modified or created data isn't rolled back.
     */

    public RealityCheckSet getOrCreateCurrentRealitySet(String guid) throws UserNotFoundException, Status500InternalServerErrorException {

        RealityCheckSet actualSet = realityCheckSetRepository.findByGuid(guid);
        if (actualSet == null) {
            try {
                User player = userApiInternalClientService.getUserByGuid(guid);
                actualSet = createRealityCheckSet(player.guid());
                realityCheckSetRepository.save(actualSet);
                List<ChangeLogFieldChange> clfc = changeLogService.copy(actualSet, new RealityCheckSet(), new String[]{"timerTime"});

                changeLogService.registerChangesForNotesWithFullNameAndDomain("user.realitycheck", "create", player.getId(),
                    guid, null, null, null, clfc, Category.RESPONSIBLE_GAMING,
                    SubCategory.REALITY_CHECK, 1, player.getDomain().getName());

            } catch (Exception | UserClientServiceFactoryException e) {
                String msg = "Changelog registration for player reality check create failed";
                log.error(msg + e.getMessage(), e);
                throw new Status500InternalServerErrorException(msg);
            }
        }
        return actualSet;
    }

    public RealityCheckSet getCurrentRealitySet(String guid) {
        return realityCheckSetRepository.findByGuid(guid);
    }

    private RealityCheckSet createRealityCheckSet(String guid) throws Status550ServiceDomainClientException {
        RealityCheckSet actualSet;
        String domainName = guid.split("/")[0];
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        Optional<String> labelValue = domain.findDomainSettingByName(DOMAIN_SETTING_REALITY_CHECK);
        long realityCheckTime;
        realityCheckTime = labelValue.map(Long::parseLong).orElse(DEFAULT_REALITY_CHECK_TIME);

        actualSet = RealityCheckSet.builder().guid(guid).timerTime(realityCheckTime).build();
        return actualSet;
    }

    public RealityCheckSet setRealityCheckTimerTime(String authorGuid, String guid, long realityCheckTime, LithiumTokenUtil util) throws UserNotFoundException, Status500InternalServerErrorException {
        RealityCheckSet actualSet = getOrCreateCurrentRealitySet(guid);
        RealityCheckSet newSet = RealityCheckSet.builder().guid(actualSet.getGuid()).id(actualSet.getId()).timerTime(realityCheckTime).build();
        if (actualSet.getTimerTime() != newSet.getTimerTime()) {
            try {
                User player = userApiInternalClientService.getUserByGuid(guid);

                List<ChangeLogFieldChange> clfc = changeLogService.copy(newSet, actualSet, new String[]{"timerTime"});
                changeLogService.registerChangesForNotesWithFullNameAndDomain("user.realitycheck", "edit", player.getId(),
                    authorGuid, util, null, null, clfc, Category.RESPONSIBLE_GAMING,
                    SubCategory.REALITY_CHECK, 1, player.getDomain().getName());

            } catch (Exception | UserClientServiceFactoryException e) {
                String msg = "Changelog registration for player reality check update failed";
                log.error(msg + e.getMessage(), e);
                throw new Status500InternalServerErrorException(msg);
            }
            realityCheckSetRepository.save(newSet);
        }
        return actualSet;
    }

    public void logUserChoice(String guid, RealityCheckStatusType action) throws Exception, UserNotFoundException, UserClientServiceFactoryException {
        User player = userApiInternalClientService.getUserByGuid(guid);

        RealityCheckTrackData rctdata = RealityCheckTrackData.builder()
                .action(action)
                .date(Timestamp.valueOf(LocalDateTime.now()))
                .guid(player.guid())
                .build();

        realityCheckDataRepository.save(rctdata);
        List<ChangeLogFieldChange> clfc = changeLogService.copy(rctdata, new RealityCheckTrackData(), new String[]{"action", "date"});

        changeLogService.registerChangesForNotesWithFullNameAndDomain("user.realitycheck.track", "create", player.getId(),
            guid, null, null, null, clfc, Category.RESPONSIBLE_GAMING,
            SubCategory.REALITY_CHECK, 1, player.getDomain().getName());
    }

    public List<Integer> getListOfDefaultTimersInMilliseconds(String domainName) throws Status550ServiceDomainClientException {
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        Optional<String> labelValue = domain.findDomainSettingByName(DOMAIN_SETTING_REALITY_CHECK_PERIODS_IN_MILLIS);
        String result = labelValue.orElse(DEFAULT_REALITY_CHECK_PERIODS_IN_MILLIS);
        List<Integer> defaultTimersInMilliseconds = new ArrayList<>();
                parseRealityCheckPeriodsInMillisSetting(result)
                        .stream()
                        .forEach(defaultTimer-> defaultTimersInMilliseconds.add(defaultTimer*60000));
        return defaultTimersInMilliseconds;
    }
    public List<Double> getListOfDefaultTimersInMinutes(String domainName) throws Status550ServiceDomainClientException {
        Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
        Optional<String> labelValue = domain.findDomainSettingByName(DOMAIN_SETTING_REALITY_CHECK_PERIODS_IN_MILLIS);
        String result = labelValue.orElse(DEFAULT_REALITY_CHECK_PERIODS_IN_MILLIS);
        return parseRealityCheckPeriodsInMillisSettingAndReturnDecimals(result);
    }

    private List<Double> parseRealityCheckPeriodsInMillisSettingAndReturnDecimals(String value) {
        List<Double> periodsInMillis = new ArrayList<>();
        String[] settings = value.split(",");
        for (String setting : settings) {
            try {
                /**
                 * Convert milliseconds to minutes as per PLAT-4852
                 */
                Double periodInMillis = (Double.parseDouble(setting.trim())/60000);
                periodsInMillis.add(periodInMillis);
            } catch (NumberFormatException nfe) {
                log.warn("Could not parse (" + setting + ") due to " + nfe.getMessage() + ". The value is ignored.");
            }
        }
        return periodsInMillis;
    }

    private List<Integer> parseRealityCheckPeriodsInMillisSetting(String value) {
        List<Integer> periodsInMillis = new ArrayList<>();
        String[] settings = value.split(",");
        for (String setting : settings) {
            try {
                /**
                 * Convert milliseconds to minutes as per PLAT-4852
                 */
                Integer periodInMillis = (Integer.parseInt(setting.trim())/60000);
                periodsInMillis.add(periodInMillis);
            } catch (NumberFormatException nfe) {
                log.warn("Could not parse (" + setting + ") due to " + nfe.getMessage() + ". The value is ignored.");
            }
        }
        return periodsInMillis;
    }

    public Page<RealityCheckTrackDataFE> getListOfTrackData(String guid, PageRequest pageRequest) {
        return mapTrackDataToTrackDataFE(realityCheckDataRepository.findAllByGuid(guid, pageRequest));
    }

    public Page<RealityCheckTrackDataFE> mapTrackDataToTrackDataFE(Page<RealityCheckTrackData> trackDataPage) {
        ArrayList<RealityCheckTrackDataFE> trackDataFEList = new ArrayList<>(trackDataPage.getSize());
        trackDataPage.forEach(data -> {
            RealityCheckTrackDataFE rctdfe = mapper.map(data, RealityCheckTrackDataFE.class);
            trackDataFEList.add(rctdfe);
        });
        return new SimplePageImpl<>(trackDataFEList, trackDataPage.getNumber(), trackDataPage.getSize(), trackDataPage.getTotalElements());
    }
    
    public RealityCheckSet findOrCreateRealityCheck(String guid, long realityCheckTime){
        RealityCheckSet actualSet = realityCheckSetRepository.findByGuid(guid);
        if (actualSet == null) {
            actualSet = createRealityCheckSet(guid);
          return realityCheckSetRepository.save(actualSet);
        }
        return realityCheckSetRepository.save(RealityCheckSet.builder()
                .id(actualSet.getId())
                .guid(guid)
                .timerTime(realityCheckTime)
                .build());
    }
}


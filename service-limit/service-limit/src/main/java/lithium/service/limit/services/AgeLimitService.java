package lithium.service.limit.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lithium.service.limit.client.exceptions.Status479DomainAgeLimitException;
import lithium.service.limit.data.dto.EditDomainAgeLimitRange;
import lithium.service.limit.data.dto.SaveDomainAgeLimitDto;
import lithium.service.limit.data.entities.DomainAgeLimit;
import lithium.service.limit.data.repositories.DomainAgeLimitRepository;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import static lithium.client.changelog.objects.ChangeLogFieldChange.formatCurrencyFields;

/**
 * The functionality in this service deals with player and domain loss/win limits via their age.
 */
@Service
@Slf4j
public class AgeLimitService {

    private final DomainAgeLimitRepository domainAgeLimitRepository;
    private final CachingDomainClientService domainService;
    private final ChangeLogService changeLogService;
    private final LithiumTokenUtilService tokenService;
    private final MessageSource messageSource;

    private static final String AMOUNT = "amount";
    private static final String AGE_MAX = "ageMax";
    private static final String AGE_MIN = "ageMin";

    @Autowired
    public AgeLimitService(DomainAgeLimitRepository domainAgeLimitRepository,
                           CachingDomainClientService domainService,
                           ChangeLogService changeLogService,
                           LithiumTokenUtilService tokenService,
                           MessageSource messageSource) {
        this.domainAgeLimitRepository = domainAgeLimitRepository;
        this.domainService = domainService;
        this.changeLogService = changeLogService;
        this.tokenService = tokenService;
        this.messageSource = messageSource;
    }

    public List<DomainAgeLimit> findAllDomainAgeLimit(String domainName) {
        return domainAgeLimitRepository.findByDomainName(domainName);
    }

    public Iterable<String> findAllDomainsWithAgeLimits(){
        Iterable<DomainAgeLimit> domainAgeLimits = domainAgeLimitRepository.findAll();
        Iterator<DomainAgeLimit> iterator = domainAgeLimits.iterator();

        List<String> domains = new ArrayList<>();

        while (iterator.hasNext()) {
            domains.add(iterator.next().getDomainName());
        }

        return new HashSet<>(domains);
    }

    public DomainAgeLimit editDomainAgeLimit(DomainAgeLimit editAgeLimit, Principal principal, String localeStr) throws Status479DomainAgeLimitException, Status550ServiceDomainClientException {
        Long domainId = domainService.retrieveDomainFromDomainService(editAgeLimit.getDomainName()).getId();

        if (domainId == null) {
            throw new Status479DomainAgeLimitException(messageSource.getMessage("SERVICE_LIMIT.DOMAINAGELIMIT.ERROR.INVALID_DOMAIN_INPUT", null, getLocale(localeStr)));
        }

        DomainAgeLimit domainAgeLimit = domainAgeLimitRepository.findOne(editAgeLimit.getId());

        if (domainAgeLimit == null) {
            throw new Status479DomainAgeLimitException(messageSource.getMessage("SERVICE_LIMIT.DOMAINAGELIMIT.ERROR.EMPTY_AGE_LIMITS", null, getLocale(localeStr)));
        }

        if (domainAgeLimit.getAmount() != editAgeLimit.getAmount() && domainAgeLimit.getGranularity() == editAgeLimit.getGranularity()) {
            DomainAgeLimit oldDomainLimit = new DomainAgeLimit();
            oldDomainLimit.setAmount(domainAgeLimit.getAmount());
            editAgeLimit.setModifiedByGuid(principal.getName());
            Boolean  isAgeModified = false;
            return editDomainAgeLimit(oldDomainLimit, editAgeLimit, principal, domainId, new String[]{AMOUNT}, localeStr, isAgeModified);
        }

        throw new Status479DomainAgeLimitException(messageSource.getMessage("SERVICE_LIMIT.DOMAINAGELIMIT.ERROR.FAILED_UPDATE", null, getLocale(localeStr)));
    }

    public void removeDomainAgeLimit(Long id, Principal principal) throws Exception {
        DomainAgeLimit domainAgeLimit = domainAgeLimitRepository.findOne(id);
        String domainAgeLimitDomainName = domainAgeLimit.getDomainName();
        Long domainId = domainService.getDomainClient().findByName(domainAgeLimitDomainName).getData().getId();
        domainAgeLimitRepository.delete(domainAgeLimit);
	    Domain domain = domainService.retrieveDomainFromDomainService(domainAgeLimit.getDomainName());
        List<ChangeLogFieldChange> clfc = changeLogService.copy(new DomainAgeLimit(), domainAgeLimit,
                new String[]{AGE_MAX, AGE_MIN, AMOUNT, "granularity", "domainName", "createdByGuid", "modifiedByGuid", "type"});
	    formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());
        changeLogService.registerChangesForNotesWithFullNameAndDomain("domain.ageLimit", "delete", domainId, principal.getName(),
                tokenService.getUtil(principal),
                null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 40, domainAgeLimitDomainName);
    }

    public void removeDomainAgeLimitGroup(List<Long> ids, Principal principal) {

        ids.forEach(id -> {
            try {
                removeDomainAgeLimit(id, principal);
            } catch (Exception e) {
                log.error("Domain Age limit delete failure. delete data is invalid, [DomainAgeLimitId: {}, author {}] Error: {}, Trace: ", id, principal.getName(), e.getMessage(), e);
            }
        });
    }

    public void removeDomainAgeLimitSingle(Long id, Principal principal) {
        try {
            removeDomainAgeLimit(id, principal);
        } catch (Exception e) {
            log.error("Domain Age limit delete failure. delete data is invalid, [DomainAgeLimitId: {}, author {}] Error: {}", id, principal.getName(), e.getMessage(), e);
        }
    }

    public List<DomainAgeLimit> saveDomainLimitGrp(List<SaveDomainAgeLimitDto> ageLimitDtoListDto, Principal principal, String locale) throws Status479DomainAgeLimitException, Status550ServiceDomainClientException {

        List<DomainAgeLimit> domainAgeLimits = new ArrayList<>();

        for (SaveDomainAgeLimitDto saveDomainAgeLimitDto : ageLimitDtoListDto) {
            if (saveDomainAgeLimitDto.getAmount() > 0) {
                domainAgeLimits.add(saveDomainLimit(saveDomainAgeLimitDto, principal, locale));
            }
        }

        return domainAgeLimits;

    }

    public List<DomainAgeLimit> editDomainLimitAgeGroup(EditDomainAgeLimitRange editDomainAgeLimitRange, Principal principal, String locale) throws Status479DomainAgeLimitException, Status550ServiceDomainClientException {

        List<DomainAgeLimit> domainAgeLimits = new ArrayList<>();

        List<DomainAgeLimit> limitsSpecificToRange = domainAgeLimitRepository.findByIdIn(editDomainAgeLimitRange.getIdsToEdit());

        List<DomainAgeLimit> allByDomainName = findAllDomainAgeLimit(editDomainAgeLimitRange.getDomainName());

        allByDomainName.removeAll(limitsSpecificToRange);
        List<DomainAgeLimit> minAgeList = isWithinAgeRange(editDomainAgeLimitRange.getNextAgeMin(), "", allByDomainName);
        List<DomainAgeLimit> maxAgeList = isWithinAgeRange(editDomainAgeLimitRange.getNextAgeMax(), "", allByDomainName);

        if (minAgeList.isEmpty() && maxAgeList.isEmpty()) {

            Long domainId = domainService.retrieveDomainFromDomainService(editDomainAgeLimitRange.getDomainName()).getId();
            AtomicReference<Boolean> isAgeModified = new AtomicReference<>(false);
            limitsSpecificToRange.forEach(editDomain -> {
                DomainAgeLimit oldDomainLimit = new DomainAgeLimit();
                oldDomainLimit.setAgeMax(editDomainAgeLimitRange.getPreviousAgeMax());
                oldDomainLimit.setAgeMin(editDomainAgeLimitRange.getPreviousAgeMin());
                editDomain.setAgeMin(editDomainAgeLimitRange.getNextAgeMin());
                editDomain.setAgeMax(editDomainAgeLimitRange.getNextAgeMax());
                editDomain.setModifiedByGuid(principal.getName());
                try {
                    domainAgeLimits.add(editDomainAgeLimit(oldDomainLimit, editDomain, principal, domainId, new String[]{AGE_MAX, AGE_MIN}, locale, isAgeModified.get()));
                    isAgeModified.set(true);
                } catch (Exception e) {
                    log.error("Domain Age limit edit failure. update data is invalid. [Stored Limit: {}, New Limit {}, Author {}, Range Min {} : Max {} ]", oldDomainLimit, editDomain, principal.getName(), AGE_MAX, AGE_MIN, e);
                }
            });
        }

        if (!minAgeList.isEmpty() || !maxAgeList.isEmpty()) {
            throw new Status479DomainAgeLimitException(messageSource.getMessage("SERVICE_LIMIT.DOMAINAGELIMIT.ERROR.STORED_AGE", null, getLocale(locale)));
        }
        return domainAgeLimits;
    }

    public List<DomainAgeLimit> isWithinAgeRange(Integer age, String domainName, List<DomainAgeLimit> storedLimitsByDomain) {
        List<DomainAgeLimit> limitsInRange = new ArrayList<>();
        List<DomainAgeLimit> storedLimits = storedLimitsByDomain.isEmpty() ? findAllDomainAgeLimit(domainName) : storedLimitsByDomain;

        if (!ObjectUtils.isEmpty(age)) {
            storedLimits.forEach(domainAgeLimit -> {
                if (age >= domainAgeLimit.getAgeMin() && age <= domainAgeLimit.getAgeMax()) {
                    limitsInRange.add(domainAgeLimit);
                }
            });
        }

        return limitsInRange;
    }


    public DomainAgeLimit saveDomainLimit(SaveDomainAgeLimitDto ageLimitDto, Principal principal, String locale) throws Status479DomainAgeLimitException, Status550ServiceDomainClientException {
        Long domain = domainService.retrieveDomainFromDomainService(ageLimitDto.getDomainName()).getId();

        if (domain == null) {
            log.error("Domain Age limit save failure. Domain is invalid {}", ageLimitDto.getDomainName());
            throw new Status479DomainAgeLimitException(messageSource.getMessage("SERVICE_LIMIT.DOMAINAGELIMIT.ERROR.INVALID_DOMAIN_INPUT", null, getLocale(locale)));
        }

        if (ageLimitDto.getAgeMin() > ageLimitDto.getAgeMax() || ageLimitDto.getAgeMax() == ageLimitDto.getAgeMin()) {
            //check if sent meets requirements
            log.error("Domain Age limit save failure. Invalid age {}", ageLimitDto.toString());
            throw new Status479DomainAgeLimitException(messageSource.getMessage("SERVICE_LIMIT.DOMAINAGELIMIT.ERROR.INVALID_AGE_INPUT", null, getLocale(locale)));
        }

        try {
            return singleRecordOperations(ageLimitDto, principal, domain, locale);
        } catch (Exception e) {
            log.error("Domain Age limit save failure. List Exception. DomainAgeLimit {}, Author {}", ageLimitDto, principal.getName(), e);
            throw new Status479DomainAgeLimitException(messageSource.getMessage("SERVICE_LIMIT.DOMAINAGELIMIT.ERROR.LIST_SAVE_ERROR", null, getLocale(locale)));
        }
    }

    private DomainAgeLimit singleRecordOperations(SaveDomainAgeLimitDto ageLimitDto, Principal principal, Long domainId, String locale) throws Status479DomainAgeLimitException, Status550ServiceDomainClientException {
        DomainAgeLimit domainAgeLimit = domainAgeLimitRepository.findByDomainNameAndAgeMaxAndAgeMinAndGranularityAndType(ageLimitDto.getDomainName(), ageLimitDto.getAgeMax(), ageLimitDto.getAgeMin(), ageLimitDto.getGranularity(), ageLimitDto.getType());

        if (domainAgeLimit == null) {
            return checkAgeRange(ageLimitDto, principal, domainId, locale);
        }

        if (domainAgeLimit.getAmount() != ageLimitDto.getAmount() && domainAgeLimit.getGranularity() == ageLimitDto.getGranularity()) {
            DomainAgeLimit oldDomainLimit = new DomainAgeLimit();
            oldDomainLimit.setAmount(domainAgeLimit.getAmount());
            domainAgeLimit.setAmount(ageLimitDto.getAmount());
            domainAgeLimit.setModifiedByGuid(principal.getName());
            Boolean isAgeModified = false;
            return editDomainAgeLimit(oldDomainLimit, domainAgeLimit, principal, domainId, new String[]{AMOUNT}, locale, isAgeModified);
        }

        throw new Status479DomainAgeLimitException(messageSource.getMessage("SERVICE_LIMIT.DOMAINAGELIMIT.ERROR.DATA_ERROR", null, getLocale(locale)));
    }

    private DomainAgeLimit checkAgeRange(SaveDomainAgeLimitDto ageLimitDto, Principal principal, Long domainId, String locale) throws Status479DomainAgeLimitException, Status550ServiceDomainClientException {
        List<DomainAgeLimit> domainAgeLimit = domainAgeLimitRepository.findByDomainNameAndGranularityAndType(ageLimitDto.getDomainName(), ageLimitDto.getGranularity(), ageLimitDto.getType());

        int index = 0;
        for (DomainAgeLimit ageLimit : domainAgeLimit) {
            index++;
            if (index == domainAgeLimit.size() && ageLimitDto.getAgeMin() >= ageLimit.getAgeMin() && ageLimitDto.getAgeMin() <= ageLimit.getAgeMax()
                    || ageLimitDto.getAgeMax() >= ageLimit.getAgeMin() && ageLimitDto.getAgeMax() <= ageLimit.getAgeMax()) {
                log.error("Domain Age limit save failure. Invalid age {}", ageLimitDto.toString());
                throw new Status479DomainAgeLimitException(messageSource.getMessage("SERVICE_LIMIT.DOMAINAGELIMIT.ERROR.STORED_AGE", null, getLocale(locale)));
            }
        }
        return addNewDomainLimit(ageLimitDto, principal, domainId, locale);
    }

    private DomainAgeLimit buildObject(SaveDomainAgeLimitDto sdald) {
        return DomainAgeLimit.builder()
                .ageMax(sdald.getAgeMax())
                .ageMin(sdald.getAgeMin())
                .amount(sdald.getAmount())
                .granularity(sdald.getGranularity())
                .domainName(sdald.getDomainName())
                .createdByGuid(sdald.getCreatorGuid())
                .modifiedByGuid(sdald.getCreatorGuid())
                .type(sdald.getType())
                .build();
    }

    private DomainAgeLimit addNewDomainLimit(SaveDomainAgeLimitDto sdald, Principal principal, Long domainId, String locale) throws Status479DomainAgeLimitException, Status550ServiceDomainClientException {
        DomainAgeLimit domainAgeLimit = buildObject(sdald);
        DomainAgeLimit stored = domainAgeLimitRepository.save(domainAgeLimit);
        Domain domain = domainService.retrieveDomainFromDomainService(domainAgeLimit.getDomainName());
        try {
            List<ChangeLogFieldChange> clfc = changeLogService.copy(stored, new DomainAgeLimit(),
                    new String[]{AGE_MAX, AGE_MIN, AMOUNT, "granularity", "domainName", "createdByGuid", "modifiedByGuid", "type"});
	        formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());
            changeLogService.registerChangesForNotesWithFullNameAndDomain("domain.ageLimit", "create", domainId, principal.getName(),
                    tokenService.getUtil(principal),
                    null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 40, domainAgeLimit.getDomainName());
        } catch (Exception e) {
            throw new Status479DomainAgeLimitException(messageSource.getMessage("SERVICE_LIMIT.DOMAINAGELIMIT.ERROR.LIST_SAVE_ERROR", null, getLocale(locale)));
        }
        return domainAgeLimit;
    }

    private DomainAgeLimit editDomainAgeLimit(DomainAgeLimit oldDomainLimit, DomainAgeLimit domainAgeLimit, Principal principal, Long domainId, String[] fields, String localeStr, Boolean isAgeModified) throws Status479DomainAgeLimitException, Status550ServiceDomainClientException {
        domainAgeLimit = domainAgeLimitRepository.save(domainAgeLimit);
        Domain domain = domainService.retrieveDomainFromDomainService(domainAgeLimit.getDomainName());
        try {
            List<ChangeLogFieldChange> clfc = changeLogService.copy(domainAgeLimit, oldDomainLimit, fields);
	        formatCurrencyFields(clfc, domain.getDefaultLocale(), domain.getCurrencySymbol(),domain.getCurrency());
            if(!isAgeModified) {
                changeLogService.registerChangesForNotesWithFullNameAndDomain("domain.ageLimit", "edit", domainId, principal.getName(),
                        tokenService.getUtil(principal),
                        null, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.LOSS_LIMITS, 40, domainAgeLimit.getDomainName());
            }
        } catch (Exception e) {
            throw new Status479DomainAgeLimitException(messageSource.getMessage("SERVICE_LIMIT.DOMAINAGELIMIT.ERROR.FAILED_UPDATE", null, getLocale(localeStr)));
        }
        return domainAgeLimit;
    }

    private Locale getLocale(String localStr) {
        localStr = localStr.replace("\\_", "-");
        return new Locale(localStr.split("-")[0], localStr.split("-")[1]);
    }

    private String getCurrencySymbol(String domainName) throws Status550ServiceDomainClientException {
        Domain domain = domainService.retrieveDomainFromDomainService(domainName);
        return domain.getCurrencySymbol();
    }
}

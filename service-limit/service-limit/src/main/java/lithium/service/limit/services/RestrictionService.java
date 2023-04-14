package lithium.service.limit.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lithium.service.limit.client.stream.UserRestrictionTriggerStream;
import lithium.service.limit.data.dto.DomainRestrictionSetDto;
import lithium.service.limit.data.entities.Domain;
import lithium.service.limit.data.entities.DomainRestriction;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.Restriction;
import lithium.service.limit.data.entities.RestrictionOutcomeLiftAction;
import lithium.service.limit.data.entities.RestrictionOutcomePlaceAction;
import lithium.service.limit.data.repositories.DomainRepository;
import lithium.service.limit.data.repositories.DomainRestrictionRepository;
import lithium.service.limit.data.repositories.DomainRestrictionSetRepository;
import lithium.service.limit.data.repositories.RestrictionOutcomeLiftActionRepository;
import lithium.service.limit.data.repositories.RestrictionOutcomePlaceActionRepository;
import lithium.service.limit.data.repositories.RestrictionRepository;
import lithium.service.limit.data.specifications.DomainRestrictionSetSpecification;
import lithium.service.limit.enums.AlternativeMessageAction;
import lithium.service.limit.enums.RestrictionType;
import lithium.service.translate.client.TranslationsService;
import lithium.service.translate.client.objects.RestrictionError;
import lithium.service.user.client.objects.RestrictionData;
import lithium.service.user.client.objects.RestrictionsMessageType;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RestrictionService {
    @Autowired
    private ChangeLogService changeLogService;
    @Autowired
    private DomainRestrictionRepository restrictionRepository;
    @Autowired
    private DomainRestrictionSetRepository setRepository;
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private RestrictionRepository restrictions;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private TranslationsService translationsService;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private UserRestrictionTriggerStream userRestrictionTriggerStream;
    @Autowired
    private RestrictionOutcomeLiftActionRepository liftActionRepository;
    @Autowired
    private RestrictionOutcomePlaceActionRepository placeActionRepository;

    public Iterable<Restriction> restrictions() {
        return restrictions.findAll();
    }

    public List<DomainRestrictionSet> findByDomainNameAndEnabledTrue(String domainName) {
        return setRepository.findByDomainNameAndEnabledTrue(domainName);
    }

    public List<DomainRestrictionSet> findByDomainNameAndEnabledTrueAndDwhVisibleTrue(String domainName) {
        return setRepository.findByDomainNameAndEnabledTrueAndDwhVisibleTrue(domainName);
    }

    public DomainRestrictionSet find(Long id) throws Status500InternalServerErrorException {
        return Optional.ofNullable(setRepository.findOne(id))
                .orElseThrow(() -> new Status500InternalServerErrorException("DomainRestrictionSet not found [id=" + id + "]"));
    }

    public String getRestrictionErrorMessageTranslation(DomainRestrictionSet set){
        return RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource, set.getDomain().getName(), set.errorMessageKey());
    }

    public DomainRestriction findDomainRestriction(Long id) throws Status500InternalServerErrorException {
        DomainRestriction domainRestriction = restrictionRepository.findOne(id);
        if (domainRestriction == null)
            throw new Status500InternalServerErrorException("DomainRestriction not found [id=" + id + "]");
        return domainRestriction;
    }

    public Page<DomainRestrictionSet> find(String[] domains, Boolean enabled, String searchValue, Pageable pageable) {
        log.debug("RestrictionService.find [domains=" + Arrays.toString(domains) + ", enabled=" + enabled
                + ", searchValue=" + searchValue);
        Specification<DomainRestrictionSet> spec = null;
        spec = addToSpec(false, spec, DomainRestrictionSetSpecification::deleted);
        spec = addToSpec(domains, spec, DomainRestrictionSetSpecification::domains);
        spec = addToSpec(enabled, spec, DomainRestrictionSetSpecification::enabled);
        spec = addToSpec(searchValue, spec, DomainRestrictionSetSpecification::any);
        Page<DomainRestrictionSet> result = setRepository.findAll(spec, pageable);
        return result;
    }

    public List<DomainRestrictionSet> find(String[] domains, Boolean enabled) {
        Specification<DomainRestrictionSet> spec = null;
        spec = addToSpec(false, spec, DomainRestrictionSetSpecification::deleted);
        spec = addToSpec(domains, spec, DomainRestrictionSetSpecification::domains);
        spec = addToSpec(enabled, spec, DomainRestrictionSetSpecification::enabled);
        return setRepository.findAll(spec);
    }

    public DomainRestrictionSet findByDomainAndName(String domain, String name) {
        return setRepository.findByDomainNameAndName(domain, name);
    }

    public DomainRestrictionSet findByDomainAndName(Domain domain, String name) {
        return setRepository.findByDomainAndName(domain, name);
    }

    @Transactional(rollbackOn = Exception.class)
    public DomainRestrictionSet create(DomainRestrictionSet set, String authorGuid)
            throws Status500InternalServerErrorException {
        log.debug("RestrictionService.create [set=" + set + ", authorGuid=" + authorGuid + "]");
        Domain domain = domainRepository.findOrCreateByName(set.getDomain().getName(), () -> new Domain());
        if (domain == null) throw new Status500InternalServerErrorException("Domain not found");
        if (setRepository.findByDomainAndName(domain, set.getName()) != null)
            throw new Status500InternalServerErrorException("A domain restriction set with this name already exists");
        set.setDomain(domain);
        set = setRepository.save(set);
        for (DomainRestriction restriction : set.getRestrictions()) {
            restriction.setSet(set);
            restriction.setRestriction(restrictions.findByCode(restriction.getRestriction().getCode()));
            restriction = restrictionRepository.save(restriction);
        }
        registerRestrictionErrorMessage(set);
        set = setRepository.save(set);

        addToRestrictionsQueue(set, RestrictionsMessageType.DOMAIN_SET_UPDATE);

        registerDomainRestrictionSetChangeLog(
                "create",
                new String[]{"domain", "name", "enabled", "deleted", "communicateToPlayer", "restrictions", "placeActions", "liftActions"},
                authorGuid,
                new DomainRestrictionSet(),
                set
        );
        return set;
    }

    @Transactional(rollbackOn = Exception.class)
    public DomainRestrictionSet changeName(DomainRestrictionSet set, String newName, String authorGuid)
            throws Status500InternalServerErrorException {
        log.debug("RestrictionService.changeName [set=" + set + ", newName=" + newName + ", authorGuid=" + authorGuid + "]");
        if (set.isSystemRestriction()) {
            throw new Status500InternalServerErrorException("This action is not permitted on system restrictions");
        }
        if (set.isDeleted()) {
            throw new Status500InternalServerErrorException("Domain restriction set not found");
        }
        if (findByDomainAndName(set.getDomain(), newName) != null) {
            throw new Status500InternalServerErrorException("A domain restriction set with this name already exists");
        }
        DomainRestrictionSet oldSet = new DomainRestrictionSet();
        oldSet.setName(String.valueOf(set.getName()));
        set.setName(newName);
        set = setRepository.save(set);

        addToRestrictionsQueue(set, RestrictionsMessageType.DOMAIN_SET_UPDATE);

        registerDomainRestrictionSetChangeLog(
                "edit",
                new String[]{"name"},
                authorGuid,
                oldSet,
                set
        );
        return set;
    }

    @Transactional(rollbackOn = Exception.class)
    public DomainRestrictionSet toggleEnabled(DomainRestrictionSet set, String authorGuid)
            throws Status500InternalServerErrorException {
        log.debug("RestrictionService.toggleEnabled [set=" + set + ", authorGuid=" + authorGuid + "]");
        if (set.isSystemRestriction()) {
            throw new Status500InternalServerErrorException("This action is not permitted on system restrictions");
        }
        if (set.isDeleted()) {
            throw new Status500InternalServerErrorException("Set not found");
        }
        boolean enabled = set.isEnabled();

        DomainRestrictionSet oldSet = new DomainRestrictionSet();
        oldSet.setName(String.valueOf(set.getName()));
        oldSet.setEnabled(enabled);

        set.setEnabled(!set.isEnabled());
        set = setRepository.save(set);

        addToRestrictionsQueue(set, RestrictionsMessageType.DOMAIN_SET_UPDATE);

        registerDomainRestrictionSetChangeLog(
                (set.isEnabled()) ? "enable" : "disable",
                new String[]{"enabled"},
                authorGuid,
                oldSet,
                set
        );
        return set;
    }

    @Transactional(rollbackOn = Exception.class)
    public DomainRestrictionSet toggleDwhVisibility(DomainRestrictionSet set, String authorGuid)
            throws Status500InternalServerErrorException {
        log.debug("RestrictionService.toggleDwhVisibility [set=" + set + ", authorGuid=" + authorGuid + "]");
        if (set.isDeleted()) {
            throw new Status500InternalServerErrorException("Set not found");
        }
        boolean dwhVisible = set.isDwhVisible();

        DomainRestrictionSet oldSet = new DomainRestrictionSet();
        oldSet.setName(String.valueOf(set.getName()));
        oldSet.setDwhVisible(dwhVisible);

        set.setDwhVisible(!set.isDwhVisible());
        set = setRepository.save(set);

        addToRestrictionsQueue(set, RestrictionsMessageType.DOMAIN_SET_UPDATE);

        registerDomainRestrictionSetChangeLog(
                (set.isDwhVisible()) ? "true" : "false",
                new String[]{"dwhVisible"},
                authorGuid,
                oldSet,
                set
        );
        return set;
    }

    @Transactional(rollbackOn = Exception.class)
    public DomainRestrictionSet toggleCommunicateToPlayer(DomainRestrictionSet set, String authorGuid)
            throws Status500InternalServerErrorException {
        log.debug("RestrictionService.toggleEnabled [set=" + set + ", authorGuid=" + authorGuid + "]");
        if (set.isDeleted()) {
            throw new Status500InternalServerErrorException("Set not found");
        }
        boolean enabled = set.isCommunicateToPlayer();

        DomainRestrictionSet oldSet = new DomainRestrictionSet();
        oldSet.setName(String.valueOf(set.getName()));
        oldSet.setCommunicateToPlayer(enabled);

        set.setCommunicateToPlayer(!set.isCommunicateToPlayer());
        set = setRepository.save(set);

        addToRestrictionsQueue(set, RestrictionsMessageType.DOMAIN_SET_UPDATE);

        registerDomainRestrictionSetChangeLog(
                (set.isCommunicateToPlayer()) ? "enable" : "disable",
                new String[]{"communicateToPlayer"},
                authorGuid,
                oldSet,
                set
        );
        return set;
    }

    @Transactional(rollbackOn = Exception.class)
    public DomainRestrictionSet deleteSet(DomainRestrictionSet set, String authorGuid)
            throws Status500InternalServerErrorException {
        log.debug("RestrictionService.deleteSet [set=" + set + ", authorGuid=" + authorGuid + "]");
        if (set.isSystemRestriction()) {
            throw new Status500InternalServerErrorException("This action is not permitted on system restrictions");
        }
        if (set.isDeleted()) {
            throw new Status500InternalServerErrorException("Set not found");
        }
        DomainRestrictionSet oldSet = new DomainRestrictionSet();
        oldSet.setDeleted(false);
        oldSet.setEnabled(Boolean.valueOf(set.isEnabled()));
        set.setDeleted(true);
        set.setEnabled(false);
        set.setName(set.getName() + "_deleted_" + new Date().getTime());
        set = setRepository.save(set);

        addToRestrictionsQueue(set, RestrictionsMessageType.DOMAIN_SET_UPDATE);

        registerDomainRestrictionSetChangeLog(
                "delete",
                new String[]{"deleted", "name", "enabled"},
                authorGuid,
                oldSet,
                set
        );
        return set;
    }

    @Transactional(rollbackOn = Exception.class)
    public DomainRestrictionSet addRestriction(DomainRestrictionSet set, DomainRestriction restriction, String authorGuid)
            throws Status500InternalServerErrorException {
        log.debug("RestrictionService.addRestriction [set=" + set + ", restriction=" + restriction
                + ", authorGuid=" + authorGuid + "]");
        if (set.isSystemRestriction()) {
            throw new Status500InternalServerErrorException("This action is not permitted on system restrictions");
        }
        if (set.isDeleted()) {
            throw new Status500InternalServerErrorException("Set not found");
        }
        DomainRestriction existingRestriction = restrictionRepository.findBySetAndRestrictionCode(set,
                restriction.getRestriction().getCode());
        if (existingRestriction != null) {
            existingRestriction.setDeleted(false);
            existingRestriction.setEnabled(restriction.isEnabled());
            restriction = existingRestriction;
        } else {
            restriction.setSet(set);
            restriction.setRestriction(restrictions.findByCode(restriction.getRestriction().getCode()));
        }
        restriction = restrictionRepository.save(restriction);
        try {
            List<ChangeLogFieldChange> clfc = changeLogService.compare(restriction, new DomainRestriction(),
                    new String[]{"set", "restriction", "enabled", "deleted"});
            changeLogService.registerChangesBlocking("DomainRestrictionSet.restriction", "create",
                    set.getId(), authorGuid, null, null, clfc, Category.ACCOUNT, SubCategory.RESTRICTION, 0, set.getDomain().getName());
        } catch (Exception e) {
            String msg = "Changelog registration for domain restrictions set restriction create failed";
            log.error(msg + " [set=" + set + ", restriction=" + restriction + ", authorGuid=" + authorGuid + "] " + e.getMessage(),
                    e);
            throw new Status500InternalServerErrorException(msg);
        }
        set.getRestrictions().add(restriction);
        return set;
    }

    @Transactional(rollbackOn = Exception.class)
    public DomainRestrictionSet updateRestriction(DomainRestrictionSet set, DomainRestriction restriction,
                                                  DomainRestriction restrictionUpdate, String authorGuid)
            throws Status500InternalServerErrorException {
        log.debug("RestrictionService.updateRestriction [set=" + set + ", restriction=" + restriction
                + ", restrictionUpdate=" + restrictionUpdate + ", authorGuid=" + authorGuid + "]");
        if (set.isSystemRestriction()) {
            throw new Status500InternalServerErrorException("This action is not permitted on system restrictions");
        }
        if (restriction.getSet().isDeleted()) {
            throw new Status500InternalServerErrorException("Domain restriction set not found");
        }
        if (restriction.isDeleted()) {
            throw new Status500InternalServerErrorException("Domain restriction not found");
        }
        DomainRestriction oldRestriction = new DomainRestriction();
        modelMapper.map(restriction, oldRestriction);
        final long restrictionId = restriction.getId();
        set.getRestrictions().removeIf(r -> r.getId().longValue() == restrictionId);
        restriction.setEnabled(restrictionUpdate.isEnabled());
        restriction.setSet(set);
        restriction.setRestriction(restrictions.findByCode(restriction.getRestriction().getCode()));
        restriction = restrictionRepository.save(restriction);
        set.getRestrictions().add(restriction);
        try {
            List<ChangeLogFieldChange> clfc = changeLogService.compare(restriction, oldRestriction,
                    new String[]{"set", "restriction", "enabled", "deleted"});
            changeLogService.registerChangesBlocking("DomainRestrictionSet.restriction", "edit",
                    set.getId(), authorGuid, null, null, clfc, Category.ACCOUNT, SubCategory.RESTRICTION, 0, set.getDomain().getName());
        } catch (Exception e) {
            String msg = "Changelog registration for domain restrictions set restriction edit failed";
            log.error(msg + " [set=" + set + ", restriction=" + restriction + ", restrictionUpdate=" + restrictionUpdate
                    + ", authorGuid=" + authorGuid + "] " + e.getMessage(), e);
            throw new Status500InternalServerErrorException(msg);
        }
        set.getRestrictions().sort(Comparator.comparingLong(DomainRestriction::getId));
        return set;
    }

    @Transactional(rollbackOn = Exception.class)
    public DomainRestrictionSet deleteRestriction(DomainRestrictionSet set, DomainRestriction restriction,
                                                  String authorGuid)
            throws Status500InternalServerErrorException {
        log.debug("RestrictionService.deleteRestriction [set=" + set + ", restriction=" + restriction
                + ", authorGuid=" + authorGuid + "]");
        if (set.isSystemRestriction()) {
            throw new Status500InternalServerErrorException("This action is not permitted on system restrictions");
        }
        if (restriction.getSet().isDeleted()) {
            throw new Status500InternalServerErrorException("Domain restriction set not found");
        }
        if (restriction.isDeleted()) {
            throw new Status500InternalServerErrorException("Domain restriction not found");
        }
        DomainRestriction oldRestriction = new DomainRestriction();
        modelMapper.map(restriction, oldRestriction);
        final long restrictionId = restriction.getId();
        set.getRestrictions().removeIf(r -> r.getId().longValue() == restrictionId);
        restriction.setDeleted(true);
        restriction.setEnabled(false);
        restriction = restrictionRepository.save(restriction);
        try {
            List<ChangeLogFieldChange> clfc = changeLogService.compare(restriction, oldRestriction,
                    new String[]{"deleted", "enabled"});
            changeLogService.registerChangesBlocking("DomainRestrictionSet.restriction", "delete",
                    set.getId(), authorGuid, null, null, clfc, Category.ACCOUNT, SubCategory.RESTRICTION, 0, set.getDomain().getName());
        } catch (Exception e) {
            String msg = "Changelog registration for domain restrictions set restriction delete failed";
            log.error(msg + " [set=" + set + ", restriction=" + restriction + ", authorGuid=" + authorGuid + "] " + e.getMessage(),
                    e);
            throw new Status500InternalServerErrorException(msg);
        }
        return set;
    }

    private Specification<DomainRestrictionSet> addToSpec(
            final String[] anArrayOfStrings, Specification<DomainRestrictionSet> spec,
            Function<String[], Specification<DomainRestrictionSet>> predicateMethod) {
        if (anArrayOfStrings != null && anArrayOfStrings.length > 0) {
            Specification<DomainRestrictionSet> localSpec = Specification.where(
                    predicateMethod.apply(anArrayOfStrings));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    private Specification<DomainRestrictionSet> addToSpec(
            final String aString, Specification<DomainRestrictionSet> spec,
            Function<String, Specification<DomainRestrictionSet>> predicateMethod) {
        if (aString != null && !aString.isEmpty()) {
            Specification<DomainRestrictionSet> localSpec = Specification.where(predicateMethod.apply(aString));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    private Specification<DomainRestrictionSet> addToSpec(
            final Boolean aBoolean, Specification<DomainRestrictionSet> spec,
            Function<Boolean, Specification<DomainRestrictionSet>> predicateMethod) {
        if (aBoolean != null) {
            Specification<DomainRestrictionSet> localSpec = Specification.where(predicateMethod.apply(aBoolean));
            spec = (spec == null) ? localSpec : spec.and(localSpec);
            return spec;
        }
        return spec;
    }

    public Response<ChangeLogs> getChangeLogs(Long id, String[] entities, int page) throws Exception {
        return changeLogService.listLimited(ChangeLogRequest.builder()
                .entityRecordId(id)
                .entities(entities)
                .page(page)
                .build()
        );
    }

    private void setupRestrictions() {
        Arrays.stream(RestrictionType.values()).forEach(restriction -> {
            restrictions.findOrCreateByCode(restriction.code(),
                    () -> Restriction.builder().name(restriction.restrictionName()).build());
        });
    }

    public void registerRestrictionErrorMessage(DomainRestrictionSet domainRestrictionSet) {
        String errorMessageKey = domainRestrictionSet.errorMessageKey();
        String errorMessageValue = domainRestrictionSet.getName();

        try {
            translationsService.registerNewTranslation(domainRestrictionSet.getDomain().getName(), "en", errorMessageKey, errorMessageValue);

            //register default alternative messages for intervention comps restrictions
            if (domainRestrictionSet.getAltMessageCount() > 0) {
                for (int altCount = 1; altCount <= domainRestrictionSet.getAltMessageCount(); altCount++) {
                    translationsService.registerNewTranslation(domainRestrictionSet.getDomain().getName(), "en", String.format("%s.%s", errorMessageKey, altCount), String.format("%s %s", domainRestrictionSet.getName(), altCount));
                }
            }

        } catch (Exception e) {
            log.error("There was a problem while registering a translation for a new default system restriction error message");
        }
    }

    @PostConstruct
    private void init() {
        setupRestrictions();
    }

    private void addToRestrictionsQueue(DomainRestrictionSet set, RestrictionsMessageType messageType) {
        try {
            RestrictionData restrictionData = RestrictionData.builder()
                    .domainRestrictionId(set.getId())
                    .domainRestrictionName(set.getName())
                    .domainName(set.getDomain().getName())
                    .enabled(set.isEnabled())
                    .deleted(set.isDeleted())
                    .messageType(messageType)
                    .build();

            userRestrictionTriggerStream.trigger(restrictionData);
        } catch (Exception ex) {
            log.error("Add DomainRestrictionSet to user restrictions stream failed: " + ex.getMessage(), ex);
        }
    }

    public DomainRestrictionSet updateAltMessageCount(long domainRestrictionSetId, AlternativeMessageAction action) throws Exception {
        DomainRestrictionSet domainRestrictionSet = find(domainRestrictionSetId);

        switch (action) {
            case DECREMENT:
                translationsService.deleteTranslationByCode(getAltMessageKeyFromSet(domainRestrictionSet));

                domainRestrictionSet.setAltMessageCount(domainRestrictionSet.getAltMessageCount() - 1);
                setRepository.save(domainRestrictionSet);


                break;
            case INCREMENT:
                domainRestrictionSet.setAltMessageCount(domainRestrictionSet.getAltMessageCount() + 1);
                setRepository.save(domainRestrictionSet);

                translationsService.registerNewTranslation(domainRestrictionSet.getDomain().getName(), "en", getAltMessageKeyFromSet(domainRestrictionSet),
                        String.format("%s %d", domainRestrictionSet.getName(), domainRestrictionSet.getAltMessageCount()));
                break;
        }

        return domainRestrictionSet;
    }

    private String getAltMessageKeyFromSet(DomainRestrictionSet set) {
        if (set == null) {
            return null;
        }

        return String.format("%s.%d.%d",
                set.errorType(), set.getId(), set.getAltMessageCount());
    }

    public List<DomainRestrictionSet> findByIds(List<Long> ids) {
        return setRepository.findAllByIdIn(ids);
    }

    public DomainRestrictionSet updatePlaceActions(DomainRestrictionSet set,
                                                   List<TransactionProcessingCode> activeActions,
                                                   LithiumTokenUtil tokenUtil) throws Status500InternalServerErrorException {

        Map<TransactionProcessingCode, RestrictionOutcomePlaceAction> currentActions = placeActionRepository.findBySet(set)
                .stream().collect(Collectors.toMap(RestrictionOutcomePlaceAction::getCode, Function.identity()));

        currentActions.entrySet().stream()
                .filter(actionEntry -> !activeActions.contains(actionEntry.getKey()))
                .forEach(actionEntry -> placeActionRepository.delete(actionEntry.getValue()));

        activeActions.stream()
                .filter(code -> !currentActions.containsKey(code))
                .forEach(code -> placeActionRepository.save(RestrictionOutcomePlaceAction.builder()
                        .set(set)
                        .code(code)
                        .build()));

        DomainRestrictionSet oldSet = copyDomainRestrictionSet(set);
        set.setPlaceActions(placeActionRepository.findBySet(set));

        registerDomainRestrictionSetChangeLog(
                "edit",
                new String[]{"domain", "name", "enabled", "dwhVisible", "deleted", "communicateToPlayer", "restrictions", "placeActions", "liftActions"},
                tokenUtil.guid(),
                oldSet,
                set
        );
        return set;
    }

    public DomainRestrictionSet updateLiftActions(DomainRestrictionSet set,
                                                  List<TransactionProcessingCode> activeActions,
                                                  LithiumTokenUtil tokenUtil) throws Status500InternalServerErrorException {


        Map<TransactionProcessingCode, RestrictionOutcomeLiftAction> currentActions = liftActionRepository.findBySet(set)
                .stream().collect(Collectors.toMap(RestrictionOutcomeLiftAction::getCode, Function.identity()));

        currentActions.entrySet().stream()
                .filter(actionEntry -> !activeActions.contains(actionEntry.getKey()))
                .forEach(actionEntry -> liftActionRepository.delete(actionEntry.getValue()));

        activeActions.stream()
                .filter(code -> !currentActions.containsKey(code))
                .forEach(code -> liftActionRepository.save(RestrictionOutcomeLiftAction.builder()
                        .set(set)
                        .code(code)
                        .build()));

        DomainRestrictionSet oldSet = copyDomainRestrictionSet(set);
        set.setLiftActions(liftActionRepository.findBySet(set));

        registerDomainRestrictionSetChangeLog(
                "edit",
                new String[]{"domain", "name", "enabled", "dwhVisible", "deleted", "communicateToPlayer", "restrictions", "placeActions", "liftActions"},
                tokenUtil.guid(),
                oldSet,
                set
        );
        return set;
    }

    private DomainRestrictionSet copyDomainRestrictionSet(DomainRestrictionSet set) {
        return DomainRestrictionSet.builder()
                .domain(set.getDomain())
                .name(set.getName())
                .enabled(set.isEnabled())
                .dwhVisible(set.isDwhVisible())
                .deleted(set.isDeleted())
                .restrictions(set.getRestrictions())
                .communicateToPlayer(set.isCommunicateToPlayer())
                .placeActions(set.getPlaceActions())
                .liftActions(set.getLiftActions())
                .build();
    }

    private void registerDomainRestrictionSetChangeLog(String type, String[] fields, String authorGuid, DomainRestrictionSet source, DomainRestrictionSet target) throws Status500InternalServerErrorException {
        try {
            List<ChangeLogFieldChange> clfc = changeLogService.compare(target, source, fields);
            changeLogService.registerChangesBlocking("DomainRestrictionSet", type,
                    target.getId(), authorGuid, null, null, clfc, Category.ACCOUNT, SubCategory.RESTRICTION, 0, target.getDomain().getName());
        } catch (Exception e) {
            String msg = "Changelog registration for domain restriction set create failed";
            log.error(msg + " [set=" + target + ", authorGuid=" + authorGuid + "] " + e.getMessage(), e);
            throw new Status500InternalServerErrorException(msg);
        }
    }

    public DomainRestrictionSet updateExcludeTag(Long id, Long excludeTagId, LithiumTokenUtil util) throws Status500InternalServerErrorException {

        DomainRestrictionSet domainRestrictionSet = null;
        try {
            domainRestrictionSet = find(id);

            ChangeLogFieldChange changeLogFieldChange = ChangeLogFieldChange.builder()
                    .field("excludeTagId")
                    .fromValue(String.valueOf(domainRestrictionSet.getExcludeTagId()))
                    .toValue(String.valueOf(excludeTagId))
                    .build();

            domainRestrictionSet.setExcludeTagId(excludeTagId);
            setRepository.save(domainRestrictionSet);

            changeLogService.registerChangesForNotesWithFullNameAndDomain("DomainRestrictionSet", "edit",
                    domainRestrictionSet.getId(), util.guid(), util, "DomainRestrictionSet was updated", "", Arrays.asList(changeLogFieldChange), Category.ACCOUNT, SubCategory.RESTRICTION, 0, domainRestrictionSet.getDomain().getName());
        } catch (Exception e) {
            String msg = "Failed to update excludeTag";
            log.error(msg + " [set=" + domainRestrictionSet + ", authorGuid=" + util
                    .userLegalName() + "] " + e.getMessage(), e);
            throw new Status500InternalServerErrorException(msg);
        }

        return domainRestrictionSet;
    }

    public DomainRestrictionSet updateTemplateName(DomainRestrictionSet domainRestrictionSet, String templateName, boolean isPlace, LithiumTokenUtil tokenUtil) {

        ChangeLogFieldChange changeLogFieldChange = ChangeLogFieldChange.builder()
                .field(isPlace ? "placeMailTemplate" : "liftMailTemplate")
                .fromValue(isPlace ? domainRestrictionSet.getPlaceMailTemplate() : domainRestrictionSet.getLiftMailTemplate())
                .toValue(String.valueOf(templateName))
                .build();

        if (isPlace) {
            domainRestrictionSet.setPlaceMailTemplate(templateName);
        } else {
            domainRestrictionSet.setLiftMailTemplate(templateName);
        }
        setRepository.save(domainRestrictionSet);

        changeLogService.registerChangesForNotesWithFullNameAndDomain("DomainRestrictionSet", "edit",
                domainRestrictionSet.getId(), tokenUtil.guid(), tokenUtil, "DomainRestrictionSet was updated", "", Arrays.asList(changeLogFieldChange), Category.ACCOUNT, SubCategory.RESTRICTION, 0, domainRestrictionSet.getDomain().getName());

        return domainRestrictionSet;
    }
}

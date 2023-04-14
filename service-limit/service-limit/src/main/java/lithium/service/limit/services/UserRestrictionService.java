package lithium.service.limit.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.exceptions.ErrorCodeException;
import lithium.exceptions.Status403AccessDeniedException;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.metrics.SW;
import lithium.metrics.TimeThisMethod;
import lithium.service.cashier.client.CashierInternalClient;
import lithium.service.cashier.client.internal.TransactionProcessingCode;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.limit.client.exceptions.Status403PlayerRestrictionDeniedException;
import lithium.service.limit.client.exceptions.Status409PlayerRestrictionConflictException;
import lithium.service.limit.client.exceptions.Status422PlayerRestrictionExclusionException;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.client.objects.PromotionRestrictionTriggerData;
import lithium.service.limit.client.stream.PromotionRestrictionTriggerStream;
import lithium.service.limit.client.stream.UserRestrictionTriggerStream;
import lithium.service.limit.data.entities.Domain;
import lithium.service.limit.data.entities.DomainRestriction;
import lithium.service.limit.data.entities.DomainRestrictionSet;
import lithium.service.limit.data.entities.LimitSystemAccess;
import lithium.service.limit.data.entities.PlayerCoolOff;
import lithium.service.limit.data.entities.PlayerExclusionV2;
import lithium.service.limit.data.entities.RestrictionOutcomeLiftAction;
import lithium.service.limit.data.entities.RestrictionOutcomePlaceAction;
import lithium.service.limit.data.entities.User;
import lithium.service.limit.data.entities.UserRestrictionSet;
import lithium.service.limit.data.repositories.DomainRepository;
import lithium.service.limit.data.repositories.UserRepository;
import lithium.service.limit.data.repositories.UserRestrictionSetRepository;
import lithium.service.limit.enums.AutoRestrictionRuleSetOutcome;
import lithium.service.limit.enums.CasinoBlockSubordinateType;
import lithium.service.limit.enums.SystemRestriction;
import lithium.service.limit.objects.AutoRestrictionRuleSetResult;
import lithium.service.limit.objects.UserRestrictionsRequest;
import lithium.service.notifications.client.objects.InboxMessagePlaceholderReplacement;
import lithium.service.notifications.client.objects.UserNotification;
import lithium.service.notifications.client.stream.NotificationStream;
import lithium.service.translate.client.objects.RestrictionError;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lithium.service.user.client.objects.RestrictionData;
import lithium.service.user.client.objects.RestrictionsMessageType;
import lithium.service.user.client.objects.UserAttributesData;
import lithium.service.user.client.service.UserApiInternalClientService;
import lithium.tokens.LithiumTokenUtil;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static lithium.service.limit.client.LimitInternalSystemService.USER_CONTRA_ACCOUNT_NOT_SET_RESTRICTION;

@Service
@Slf4j
public class UserRestrictionService {
    @Autowired
    private RestrictionService restrictionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserRestrictionSetRepository userRestrictionSetRepository;
    @Autowired
    private UserApiInternalClientService userApiInternalClientService;
    @Autowired
    private DomainRepository domainRepository;
    @Autowired
    private ChangeLogService changeLogService;
    @Autowired
    private LimitSystemAccessService limitSystemAccessService;
    @Autowired
    private UserRestrictionTriggerStream userRestrictionTriggerStream;
    @Autowired
    private PromotionRestrictionTriggerStream promotionRestrictionTriggerStream;
    @Autowired
    private NotificationStream notificationStream;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private PubSubUserAccountChangeProxy pubSubUserAccountChangeProxy;
    @Autowired
    private RestrictionPlayerCommService restrictionPlayerCommService;
    @Autowired
    private AutoRestrictionPlayerCommService autoRestrictionPlayerCommService;
    @Autowired
    private CachingDomainClientService cachingDomainClientService;
    @Autowired
    @Setter
    LithiumServiceClientFactory serviceClientFactory;
    @Autowired
    private ExclusionService exclusionService;
    @Autowired
    private CoolOffService coolOffService;

    private static final String RESTRICTION_CASINO = "CASINO";
    private static final String RESTRICTION_LOGIN = "LOGIN";
    private static final String RESTRICTION_DEPOSIT = "DEPOSIT";
    private static final String RESTRICTION_WITHDRAW = "WITHDRAW";
    private static final String RESTRICTION_BET_PLACEMENT = "BET_PLACEMENT";
    private static final String RESTRICTION_COMPS = "COMPS";
    private static final String RESTRICTION_F2P = "F2P";

    @TimeThisMethod
    public Access checkAccess(String playerGuid) throws Status500InternalServerErrorException {
        SW.start("getUserRestrictions");
        Access userRestrictions = getUserRestrictions(playerGuid);
        SW.stop();
        SW.start("getVerificationStatusRestrictions");
        Access verificationStatusRestrictions = getVerificationStatusRestrictions(playerGuid);
        SW.stop();

        Access access = Access.builder().build();

        access.setCasinoAllowed(verificationStatusRestrictions.isCasinoAllowed() == false ? false : userRestrictions.isCasinoAllowed());
        access.setCasinoSystemPlaced(userRestrictions.isCasinoSystemPlaced());
        access.setCasinoErrorMessage(!verificationStatusRestrictions.isCasinoAllowed() ? verificationStatusRestrictions.getCasinoErrorMessage() : !userRestrictions.isCasinoAllowed() ? userRestrictions.getCasinoErrorMessage() : null);

        access.setLoginAllowed(verificationStatusRestrictions.isLoginAllowed() == false ? false : userRestrictions.isLoginAllowed());
        access.setLoginErrorMessage(!verificationStatusRestrictions.isLoginAllowed() ? verificationStatusRestrictions.getLoginErrorMessage() : !userRestrictions.isLoginAllowed() ? userRestrictions.getLoginErrorMessage() : null);

        access.setDepositAllowed(verificationStatusRestrictions.isDepositAllowed() == false ? false : userRestrictions.isDepositAllowed());
        access.setDepositErrorMessage(!verificationStatusRestrictions.isDepositAllowed() ? verificationStatusRestrictions.getDepositErrorMessage() : !userRestrictions.isDepositAllowed() ? userRestrictions.getDepositErrorMessage() : null);

        access.setWithdrawAllowed(verificationStatusRestrictions.isWithdrawAllowed() == false ? false : userRestrictions.isWithdrawAllowed());
        access.setWithdrawErrorMessage(!verificationStatusRestrictions.isWithdrawAllowed() ? verificationStatusRestrictions.getWithdrawErrorMessage() : !userRestrictions.isWithdrawAllowed() ? userRestrictions.getWithdrawErrorMessage() : null);

        access.setBetPlacementAllowed(verificationStatusRestrictions.isBetPlacementAllowed() == false ? false : userRestrictions.isBetPlacementAllowed());
        access.setBetPlacementErrorMessage(!verificationStatusRestrictions.isBetPlacementAllowed() ? verificationStatusRestrictions.getBetPlacementErrorMessage() : !userRestrictions.isBetPlacementAllowed() ? userRestrictions.getBetPlacementErrorMessage() : null);

        access.setCompsAllowed(userRestrictions.isCompsAllowed());
        access.setCompsSystemPlaced(userRestrictions.isCompsSystemPlaced());
        access.setCompsErrorMessage(userRestrictions.getCompsErrorMessage());

        access.setF2pAllowed(userRestrictions.isF2pAllowed());
        access.setF2pErrorMessage(userRestrictions.getF2pErrorMessage());

        return access;
    }

    private Access getUserRestrictions(String userGuid) {
        String userDetails[] = userGuid.split("/");
        String domainName = userDetails[0];
        Access access = Access.builder().build();
        List<UserRestrictionSet> userRestrictionSets = getAppliedUserRestrictions(userGuid, null);
        for (UserRestrictionSet userRestrictionSet : userRestrictionSets) {
            DomainRestrictionSet domainRestrictionSet = userRestrictionSet.getSet();

            if (!domainRestrictionSet.isDeleted() &&
                    domainRestrictionSet.isEnabled()) {
                domainRestrictionSet.getRestrictions()
                        .stream()
                        .filter(domainRestriction -> {
                            return (!domainRestriction.isDeleted() &&
                                    domainRestriction.isEnabled());
                        }).forEach(domainRestriction -> {
                            switch (domainRestriction.getRestriction().getCode()) {
                                case RESTRICTION_LOGIN:
                                    access.setLoginAllowed(false);
                                    access.setLoginErrorMessage(RestrictionError.LOGIN.getResponseMessageLocal(messageSource,
                                            domainName, domainRestrictionSet.errorMessageKey()));
                                    break;
                                case RESTRICTION_DEPOSIT:
                                    access.setDepositAllowed(false);
                                    access.setDepositErrorMessage(RestrictionError.DEPOSIT.getResponseMessageLocal(messageSource,
                                            domainName, domainRestrictionSet.errorMessageKey()));
                                    break;
                                case RESTRICTION_WITHDRAW:
                                    access.setWithdrawAllowed(false);
                                    access.setWithdrawErrorMessage(RestrictionError.WITHDRAW.getResponseMessageLocal(messageSource,
                                            domainName, domainRestrictionSet.errorMessageKey()));
                                    break;
                                case RESTRICTION_BET_PLACEMENT:
                                    access.setBetPlacementAllowed(false);
                                    access.setBetPlacementErrorMessage(RestrictionError.BET_PLACEMENT.getResponseMessageLocal(messageSource,
                                            domainName, domainRestrictionSet.errorMessageKey()));
                                    break;
                                case RESTRICTION_CASINO:
                                    checkCasinoRestriction(userRestrictionSet, access);
                                    break;
                                case RESTRICTION_COMPS:
                                    checkCompsRestriction(userRestrictionSet, access);
                                    break;
                                case RESTRICTION_F2P:
                                    access.setF2pAllowed(false);
                                    access.setF2pErrorMessage(RestrictionError.F2P.getResponseMessageLocal(messageSource,
                                        domainName, domainRestrictionSet.errorMessageKey()));
                                default:
                                    break;
                            }
                        });
            }
        }
        return access;
    }

    private void checkCasinoRestriction(UserRestrictionSet userRestrictionSet, Access access) {
        if (!isCasinoRestriction(userRestrictionSet.getSet())) {
            return;
        }
        if (access.isCasinoSystemPlaced() && access.getCasinoErrorMessage() != null) {
            return;
        }

        access.setCasinoAllowed(false);
        if (!access.isCasinoSystemPlaced()) {
            access.setCasinoSystemPlaced(isSystemPlacedCasinoBlock(userRestrictionSet.getSet()));
        }

        StringBuilder translationKeybuilder = new StringBuilder(userRestrictionSet.getSet().errorMessageKey());
        if (userRestrictionSet.getSubType() != null) {
            translationKeybuilder = translationKeybuilder.append(".").append(userRestrictionSet.getSubType());
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");

        String errorMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                userRestrictionSet.getSet().getDomain().getName(), translationKeybuilder.toString(),
                userRestrictionSet.getSet().getName(), new Object[]{dateFormat.format(userRestrictionSet.getActiveFrom())});
        if (userRestrictionSet.getActiveTo() != null) {
            Long periodInDays = getRestrictionPeriodInDays(userRestrictionSet);

            errorMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                    userRestrictionSet.getSet().getDomain().getName(), translationKeybuilder.toString(),
                    userRestrictionSet.getSet().getName(), new Object[]{dateFormat.format(userRestrictionSet.getActiveFrom()), periodInDays.toString()});

        }
        access.setCasinoErrorMessage(errorMessage);
    }

    private long getRestrictionPeriodInDays(UserRestrictionSet userRestrictionSet) {
        long diffInMilliSeconds = userRestrictionSet.getActiveTo().getTime() - userRestrictionSet.getActiveFrom().getTime();
        return TimeUnit.DAYS.convert(diffInMilliSeconds, TimeUnit.MILLISECONDS);
    }

    private Boolean isSystemPlacedCasinoBlock(DomainRestrictionSet set) {
        return SystemRestriction.INTERVENTION_CASINO_BLOCK.restrictionName().equals(set.getName());
    }

    private Boolean isPlayerCasinoBlock(DomainRestrictionSet set) {
        return SystemRestriction.PLAYER_CASINO_BLOCK.restrictionName().equals(set.getName());
    }

    public void checkCompsRestriction(UserRestrictionSet userRestrictionSet, Access access) {
        access.setCompsAllowed(false);
        String errorMessage;

        List<UserRestrictionSet> activeComps = getActiveCompsRestrictions(userRestrictionSet.getUser().getGuid());

        boolean hasSystemCompsBlock = activeComps
                .stream()
                .anyMatch(userRestriction ->
                        isCompsRestrictionOfType(userRestrictionSet.getSet(), SystemRestriction.UNDERAGE_COMPS_BLOCK) || isCompsRestrictionOfType(userRestrictionSet.getSet(), SystemRestriction.INTERVENTION_COMPS_BLOCK));

        Optional<UserRestrictionSet> interventionComp = activeComps
                .stream()
                .filter(userRestriction -> isDomainRestrictionInterventionCompsBlock(userRestriction.getSet()))
                .findFirst();

        if (hasSystemCompsBlock) {
            access.setCompsSystemPlaced(true);
        }

        if (interventionComp.isPresent()) {
            UserRestrictionSet interventionUserRestriction = interventionComp.get();
            Integer subtype = interventionUserRestriction.getSubType();

            StringBuilder translationKeybuilder = new StringBuilder(interventionUserRestriction.getSet().errorType())
                    .append(".")
                    .append(interventionUserRestriction.getSet().getId());

            if (subtype != null) {
                translationKeybuilder = translationKeybuilder.append(".").append(subtype);
            }

            errorMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                    interventionUserRestriction.getSet().getDomain().getName(), translationKeybuilder.toString(), interventionUserRestriction.getSet().errorMessageKey());


        } else {
            errorMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                    userRestrictionSet.getSet().getDomain().getName(), userRestrictionSet.getSet().errorMessageKey());
        }

        access.setCompsErrorMessage(errorMessage);
    }

    public UserRestrictionSet find(Long id) throws Status500InternalServerErrorException {
        UserRestrictionSet set = userRestrictionSetRepository.findOne(id);
        if (set == null)
            throw new Status500InternalServerErrorException("UserRestrictionSet not found [id=" + id + "]");
        return set;
    }

    public UserRestrictionSet find(String userGuid, DomainRestrictionSet domainRestrictionSet) {
        return userRestrictionSetRepository.findByUserGuidAndSet(userGuid, domainRestrictionSet);
    }

    private List<UserRestrictionSet> get(String userGuid) {
        return userRestrictionSetRepository.findByUserGuid(userGuid);
    }

    public List<UserRestrictionSet> getActiveUserRestrictions(String userGuid, DateTime now) {
        List<UserRestrictionSet> userRestrictions = get(userGuid);
        DateTime finalNow = now == null ? DateTime.now() : now;
        return userRestrictions.stream().filter(ur -> isActiveUserRestriction(ur, finalNow)).collect(Collectors.toList());
    }

    public List<UserRestrictionSet> getAppliedUserRestrictions(String userGuid, DateTime now) {
        DateTime finalNow = now == null ? DateTime.now() : now;
        List<UserRestrictionSet> userRestrictions = get(userGuid);
        return userRestrictions.stream().filter(ur -> isAppliedUserRestriction(ur, finalNow)).collect(Collectors.toList());
    }

    public boolean isAppliedUserRestriction(UserRestrictionSet userRestrictionSet) {
        return isAppliedUserRestriction(userRestrictionSet, null);
    }

    public boolean isAppliedUserRestriction(UserRestrictionSet userRestrictionSet, DateTime now) {
        if (now == null) now = DateTime.now();
        return !now.isBefore(userRestrictionSet.getActiveFrom().getTime()) && (userRestrictionSet.getActiveTo() == null || now.isBefore(userRestrictionSet.getActiveTo().getTime()));
    }

    public boolean isActiveUserRestriction(UserRestrictionSet userRestrictionSet) {
        return isActiveUserRestriction(userRestrictionSet, null);
    }

    public boolean isActiveUserRestriction(UserRestrictionSet userRestrictionSet, DateTime now) {
        if (now == null) now = DateTime.now();
        DateTime activeFrom = new DateTime(userRestrictionSet.getActiveFrom());
        return userRestrictionSet.getActiveTo() == null || now.isBefore(userRestrictionSet.getActiveTo().getTime()) && activeFrom.isBefore(userRestrictionSet.getActiveTo().getTime());
    }


    public List<DomainRestrictionSet> getEligibleRestrictionSetsForUser(String userGuid) {

        List<DomainRestrictionSet> sets = new ArrayList<>();

        try {
            List<DomainRestrictionSet> setsOnUserAccount = userRestrictionSetRepository.findByUserGuid(userGuid)
                    .stream()
                    .filter(this::isActiveUserRestriction)
                    .map(userRestrictionSet -> {
                        return userRestrictionSet.getSet();
                    })
                    .collect(Collectors.toList());

            sets = restrictionService.findByDomainNameAndEnabledTrue(
                    userGuid.split("/")[0]);

            sets.removeIf(set -> setsOnUserAccount.contains(set));

            lithium.service.user.client.objects.User user = userApiInternalClientService.getUserByGuid(userGuid);

            sets.removeIf(set -> playerHasExcludedTagsForRestriction(set, user));

        } catch (UserClientServiceFactoryException | UserNotFoundException e) {
            log.error(String.format("Failed while fetching user %s, skipping filtering domain restriction set by tags", userGuid), e);
        }

        return sets;
    }

    public UserRestrictionSet place(String userGuid, DomainRestrictionSet set, String authorGuid, String comment, long userId, Integer subType, LithiumTokenUtil util) throws Status403PlayerRestrictionDeniedException, Status500InternalServerErrorException, Status409PlayerRestrictionConflictException, Status422PlayerRestrictionExclusionException {
        DateTime now = DateTime.now();
        DateTime activeTo = null;
        if (isCasinoRestriction(set) && subType != null) {
            activeTo = getCasinoActiveTo(now, subType);
        }
        return place(userGuid, set, authorGuid, comment, userId, util, now, activeTo, now, subType);
    }

    private DateTime getCasinoActiveTo(DateTime activeFrom, Integer subType) {
        Integer numberOfDays = CasinoBlockSubordinateType.getDays(subType);
        return activeFrom.plusDays(numberOfDays);
    }

    @Transactional(rollbackFor = Exception.class)
    public UserRestrictionSet place(String userGuid, DomainRestrictionSet set, String authorGuid, String comment, long userId, LithiumTokenUtil util, DateTime activeFrom, DateTime activeTo, DateTime now, Integer subType) throws Status500InternalServerErrorException, Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException, Status422PlayerRestrictionExclusionException {
        log.debug("UserRestrictionService.set [userGuid=" + userGuid + ", activeFrom=" + activeFrom + ", activeTo=" + activeTo + ", set=" + set + "]");

        validateSystemRestrictionPlace(set, userGuid);

        if (now == null) now = DateTime.now();
        activeFrom = activeFrom == null ? now : activeFrom;

        if (activeTo != null && !activeTo.isAfter(activeFrom)) {
            log.error("Failed to set restriction incorrect activeTo date. [userGuid=" + userGuid + ", activeFrom=" + activeFrom + ", activeTo=" + activeTo + ", set=" + set + "]");
            throw new Status500InternalServerErrorException("Failed to set restriction incorrect activeTo date");
        }

        User user = userRepository.findOrCreateByGuid(userGuid, () -> new User());
        UserRestrictionSet userRestrictionSet = userRestrictionSetRepository.findByUserAndSet(user, set);
        if (userRestrictionSet != null) {
            if (isActiveUserRestriction(userRestrictionSet, now) && !isDomainRestrictionInterventionCompsBlock(userRestrictionSet.getSet())) {
                if (activeFrom.isBefore(userRestrictionSet.getActiveFrom().getTime())) {
                    userRestrictionSet.setActiveFrom(activeFrom.toDate());
                }

                if (userRestrictionSet.getActiveTo() != null && (activeTo == null || activeTo.isAfter(userRestrictionSet.getActiveTo().getTime()))) {
                    userRestrictionSet.setActiveTo(activeTo != null ? activeTo.toDate() : null);
                }

                log.debug("Restriction was already applied to user. [userGuid=" + userGuid + ", activeFrom=" + activeFrom + ", activeTo=" + activeTo + ", set=" + set + "]");
                return null;
            } else {
                deleteAndSyncQueue(userRestrictionSet);
            }
        }

        userRestrictionSet = UserRestrictionSet.builder()
                .user(user)
                .activeFrom(activeFrom.toDate())
                .activeTo(activeTo != null ? activeTo.toDate() : null)
                .set(set)
                .createdOn(now.toDate())
                .subType(subType)
                .build();

        userRestrictionSet = updateAndSyncQueue(userRestrictionSet);
        triggerPubSubAccountChanges(userGuid, userRestrictionSet);

        for (DomainRestriction restriction : userRestrictionSet.getSet().getRestrictions()) {
            try {
                List<ChangeLogFieldChange> clfc = changeLogService.compare(userRestrictionSet, new UserRestrictionSet(), new String[]{"activeFrom", "activeTo", "createdOn"});
                clfc.add(ChangeLogFieldChange.builder()
                        .field(SubCategory.RESTRICTION.getName())
                        .fromValue(null)
                        .toValue(set.getName())
                        .build());

                if (subType != null) {
                    clfc.add(ChangeLogFieldChange.builder()
                            .field("subType")
                            .toValue(subType.toString())
                            .build());
                }

                String entity = "user.restriction." + restriction.getRestriction().getCode();
                changeLogService.registerChangesForNotesWithFullNameAndDomain(entity, "create", userId, authorGuid, util,
                        comment, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.RESTRICTION, 40, user.getGuid().substring(0, user.getGuid().indexOf('/')));

            } catch (Exception ex) {
                String msg = "Note registration for user restriction set failed";
                log.error(msg + "[userRestrictionSet=" + userRestrictionSet + "] " + ex.getMessage(), ex);
                throw new Status500InternalServerErrorException(msg);
            }
        }

        PlayerExclusionV2 playerExclusion = exclusionService.lookup(userGuid);
        PlayerCoolOff playerCoolingOff = coolOffService.lookup(userGuid);
        if (playerExclusion == null && (playerCoolingOff == null || !playerCoolingOff.getExpiryDate().after(new Date()))) {
            restrictionPlayerCommService.communicateWithPlayer(getUser(userGuid), userRestrictionSet, AutoRestrictionRuleSetOutcome.PLACE.name());
        }

        promotionUpdateSync(userRestrictionSet, true);
        interventionNotificationSync(userRestrictionSet);

        applyPlaceActions(user.getGuid(), set.getPlaceActions(), comment);

        return userRestrictionSet;
    }


    /**
     * The initial purpose of this method is for the VB migration. Use with care. This will not do all the necessary
     * things for the normal workflow.
     */
    public UserRestrictionSet placeBasicRemoveDuplicate(String playerGuid, DomainRestrictionSet domainRestrictionSet) {
        User user = userRepository.findOrCreateByGuid(playerGuid, () -> new User());
        UserRestrictionSet userRestrictionSet = userRestrictionSetRepository.findByUserAndSet(user,
                domainRestrictionSet);
        if (userRestrictionSet != null) {
            userRestrictionSetRepository.delete(userRestrictionSet);
        }
        return userRestrictionSetRepository.save(
                UserRestrictionSet.builder()
                        .user(user)
                        .set(domainRestrictionSet)
                        .build()
        );
    }

    public void lift(String userGuid, DomainRestrictionSet set, String authorGuid, String comment, long userId, LithiumTokenUtil util) throws Status403PlayerRestrictionDeniedException, Status500InternalServerErrorException, Status409PlayerRestrictionConflictException {
        lift(userGuid, set, authorGuid, comment, userId, util, null, null);
    }

    @Transactional(rollbackFor = Exception.class)
    public void lift(String userGuid, DomainRestrictionSet set, String authorGuid, String comment, long userId, LithiumTokenUtil util, DateTime activeTo, DateTime now) throws Status500InternalServerErrorException, Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException {
        log.debug("UserRestrictionService.lift [userGuid=" + userGuid + ", activeTo=" + activeTo + ", set=" + set + "]");

        validateRestrictionLift(set, userGuid);
        if (now == null) now = DateTime.now();
        activeTo = activeTo == null ? now : activeTo;

        User user = userRepository.findOrCreateByGuid(userGuid, () -> new User());
        UserRestrictionSet userRestrictionSet = userRestrictionSetRepository.findByUserAndSet(user, set);

        if (userRestrictionSet != null && !isActiveUserRestriction(userRestrictionSet, now)) {
            deleteAndSyncQueue(userRestrictionSet);
            triggerPubSubAccountChanges(userGuid, userRestrictionSet);
            userRestrictionSet = null;
        }

        if (userRestrictionSet == null) {
            log.debug("No active user restriction. [userGuid=" + userGuid + ", activeTo=" + activeTo + ", set=" + set + "]");
            return;
        } else if (isActiveUserRestriction(userRestrictionSet, now)) {
            if (!activeTo.isAfter(now) || activeTo.isBefore(userRestrictionSet.getActiveFrom().getTime())) {
                deleteAndSyncQueue(userRestrictionSet);
                triggerPubSubAccountChanges(userGuid, userRestrictionSet);
            } else if (activeTo.isBefore(userRestrictionSet.getActiveTo().getTime())) {
                userRestrictionSet.setActiveTo(activeTo.toDate());
                userRestrictionSet = updateAndSyncQueue(userRestrictionSet);
            }
            for (DomainRestriction restriction : userRestrictionSet.getSet().getRestrictions()) {
                try {
                    List<ChangeLogFieldChange> clfc = changeLogService.compare(new UserRestrictionSet(), userRestrictionSet, new String[]{"activeFrom", "activeTo", "createdOn"});
                    clfc.add(ChangeLogFieldChange.builder()
                            .field(SubCategory.RESTRICTION.getName())
                            .fromValue(userRestrictionSet.getSet().getName())
                            .toValue(null)
                            .build());

                    if (userRestrictionSet.getSubType() != null) {
                        clfc.add(ChangeLogFieldChange.builder()
                                .field("subType")
                                .fromValue(userRestrictionSet.getSubType().toString())
                                .toValue(null)
                                .build());
                    }
                    String entity = "user.restriction." + restriction.getRestriction().getCode();
                    changeLogService.registerChangesForNotesWithFullNameAndDomain(entity, "delete", userId, authorGuid, util,
                            comment, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.RESTRICTION, 40, user.getGuid().substring(0, user.getGuid().indexOf('/')));

                } catch (Exception ex) {
                    String msg = "Note registration for user restriction lift failed";
                    log.error(msg + " [userRestrictionSet=" + set + "] " + ex.getMessage(), ex);
                    throw new Status500InternalServerErrorException(msg);
                }
            }

            PlayerExclusionV2 playerExclusion = exclusionService.lookup(userGuid);
            PlayerCoolOff playerCoolingOff = coolOffService.lookup(userGuid);
            if (playerExclusion == null && (playerCoolingOff == null || !playerCoolingOff.getExpiryDate().after(new Date()))) {
                restrictionPlayerCommService.communicateWithPlayer(getUser(userGuid), userRestrictionSet, AutoRestrictionRuleSetOutcome.LIFT.name());
            }

            promotionUpdateSync(userRestrictionSet, false);
            processUnderAgeCompsEmail(userRestrictionSet);
        }

        applyLiftActions(user.getGuid(), set.getLiftActions(), comment);
    }

    private void triggerPubSubAccountChanges(String userGuid, UserRestrictionSet userRestrictionSet) {
        if (userRestrictionSet.getSet().getName().equalsIgnoreCase(USER_CONTRA_ACCOUNT_NOT_SET_RESTRICTION)) {
            pubSubUserAccountChangeProxy.listenAccountChanges(userGuid);
        }
    }

    private Access getVerificationStatusRestrictions(String playerGuid) throws Status500InternalServerErrorException {
        Access access = Access.builder().build();

        lithium.service.user.client.objects.User user = getUser(playerGuid);

        Long verificationStatusId = user.getVerificationStatus();
        if (verificationStatusId == null) {
            verificationStatusId = LimitSystemAccessService.UNVERIFIED_LEVEL_ID;
        }
        String[] userGuid = playerGuid.split("/");
        String domainName = userGuid[0];

        Iterable<LimitSystemAccess> limits;

        try {
            Domain domain = domainRepository.findOrCreateByName(domainName, () -> new Domain());
            limits = limitSystemAccessService.getListLimits(domain);
        } catch (Exception e) {
            log.error("Error getting limits for domain={}", domainName, e);
            throw new Status500InternalServerErrorException("Cant get limits by domain [name=" + domainName + "]");
        }

        Iterator<LimitSystemAccess> iter = limits.iterator();

        LimitSystemAccess limit = null;
        String errorDictionary = "ERROR_DICTIONARY.LIMIT_SYSTEM_ACCESS.";

        while (iter.hasNext()) {
            LimitSystemAccess currentLimit = iter.next();

            if (currentLimit.getVerificationStatus().getId().equals(verificationStatusId)) {
                limit = currentLimit;
                errorDictionary += currentLimit.getVerificationStatus().getCode().replace(" ", "_") + ".";
                break;
            }
        }
        if (limit == null) {
            throw new Status500InternalServerErrorException("Verification status not found [id=" + verificationStatusId + "]");
        }

        access.setLoginAllowed(limit.getLogin());
        access.setLoginErrorMessage(RestrictionError.LOGIN.getResponseMessageLocal(messageSource, domainName,
                errorDictionary + RESTRICTION_LOGIN));

        access.setDepositAllowed(limit.getDeposit());
        access.setDepositErrorMessage(RestrictionError.DEPOSIT.getResponseMessageLocal(messageSource, domainName,
                errorDictionary + RESTRICTION_DEPOSIT));

        access.setWithdrawAllowed(limit.getWithdraw());
        access.setWithdrawErrorMessage(RestrictionError.WITHDRAW.getResponseMessageLocal(messageSource, domainName,
                errorDictionary + RESTRICTION_WITHDRAW));

        access.setBetPlacementAllowed(limit.getBetPlacement());
        access.setBetPlacementErrorMessage(RestrictionError.BET_PLACEMENT.getResponseMessageLocal(messageSource, domainName,
                errorDictionary + RESTRICTION_BET_PLACEMENT));

        access.setCasinoAllowed(limit.getCasino());
        access.setCasinoErrorMessage(RestrictionError.CASINO.getResponseMessageLocal(messageSource, domainName,
                errorDictionary + RESTRICTION_CASINO));

        return access;
    }

    private lithium.service.user.client.objects.User getUser(String userGuid) throws Status500InternalServerErrorException {
        lithium.service.user.client.objects.User user;
        try {
            user = userApiInternalClientService.getUserByGuid(userGuid);
        } catch (UserClientServiceFactoryException e) {
            throw new Status500InternalServerErrorException("User not found [userGuid=" + userGuid + "]");
        } catch (UserNotFoundException e) {
            throw new Status500InternalServerErrorException("User not found [userGuid=" + userGuid + "]");
        }
        if (user == null) {
            throw new Status500InternalServerErrorException("User not found [userGuid=" + userGuid + "]");
        }
        return user;
    }

    private void addToUserRestrictionsQueue(UserRestrictionSet set, RestrictionsMessageType messageType) {
        try {
            DomainRestrictionSet domainSet = set.getSet();
            RestrictionData restrictionData = RestrictionData.builder()
                    .domainRestrictionId(domainSet.getId())
                    .domainRestrictionName(domainSet.getName())
                    .guid(set.getUser().getGuid())
                    .domainName(domainSet.getDomain().getName())
                    .enabled(domainSet.isEnabled())
                    .deleted(domainSet.isDeleted())
                    .activeFrom(set.getActiveFrom())
                    .activeTo(set.getActiveTo())
                    .messageType(messageType)
                    .subType(set.getSubType())
                    .build();
            userRestrictionTriggerStream.trigger(restrictionData);
        } catch (Exception ex) {
            log.error("Add UserRestrictionSet to user restrictions stream failed: " + ex.getMessage(), ex);
        }
    }

    private void deleteAndSyncQueue(UserRestrictionSet userRestrictionSet) {
        userRestrictionSetRepository.delete(userRestrictionSet);
        addToUserRestrictionsQueue(userRestrictionSet, RestrictionsMessageType.USER_SET_DELETE);
    }

    private UserRestrictionSet updateAndSyncQueue(UserRestrictionSet userSet) {
        UserRestrictionSet userRestrictionSet = userRestrictionSetRepository.save(userSet);
        addToUserRestrictionsQueue(userRestrictionSet, RestrictionsMessageType.USER_SET_UPDATE);
        return userRestrictionSet;
    }

    public void processUserAttributesData(UserAttributesData data) {
        User user = Optional.ofNullable(userRepository.findByGuid(data.getGuid()))
                .orElse(User.builder().guid(data.getGuid()).build());
        user.setTestAccount(data.isTestAccount());
        userRepository.save(user);
    }

    private void promotionUpdateSync(UserRestrictionSet userRestrictionSet, boolean restrict) {
        DomainRestrictionSet domainRestrictionSet = userRestrictionSet.getSet();

        if (domainRestrictionSet.isSystemRestriction() && isCompsRestriction(userRestrictionSet.getSet())) {
            PromotionRestrictionTriggerData data = PromotionRestrictionTriggerData.builder()
                    .restrict(restrict)
                    .userGuid(userRestrictionSet.getUser().getGuid())
                    .domainRestrictionSetId(domainRestrictionSet.getId())
                    .build();
            promotionRestrictionTriggerStream.trigger(data);
        }
    }

    private void interventionNotificationSync(UserRestrictionSet userRestrictionSet) {
        DomainRestrictionSet domainRestrictionSet = userRestrictionSet.getSet();

        if (domainRestrictionSet.isSystemRestriction() && isCompsRestriction(domainRestrictionSet) && isDomainRestrictionInterventionCompsBlock(domainRestrictionSet)) {

            Integer subType = userRestrictionSet.getSubType();

            if (subType != null) {
                List<InboxMessagePlaceholderReplacement> phReplacements = new ArrayList<>();
                phReplacements.add(InboxMessagePlaceholderReplacement.builder().key("%translationKey%").value(String.format("%s.%s.%s", userRestrictionSet.getSet().errorType(),
                        userRestrictionSet.getSet().getId(), subType)).build());

                notificationStream.process(
                        UserNotification.builder()
                                .userGuid(userRestrictionSet.getUser().getGuid())
                                .notificationName("intervention_message." + subType) // intervention_message.x where x = subType loaded on userRestrictionSet
                                .phReplacements(phReplacements)
                                .cta(true)
                                .build());
            }
        }
    }

    public void validateSystemRestrictionPlace(DomainRestrictionSet domainRestrictionSet, String userGuid) throws Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException, Status422PlayerRestrictionExclusionException {
        if (domainRestrictionSet.getExcludeTagId() != null && isPlayerExcludedFromRestriction(domainRestrictionSet, userGuid)) {

            String translationKey = "SERVICE-LIMIT.RESTRICTIONS.ERROR_MESSAGES.EXCLUSION_ERROR";

            String translatedMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                    domainRestrictionSet.getDomain().getName(), translationKey, translationKey);

            throw new Status422PlayerRestrictionExclusionException(translatedMessage);
        }

        if (domainRestrictionSet.isSystemRestriction()) {
            if (isCompsRestriction(domainRestrictionSet)) {
                validateCompsRestrictionPlace(domainRestrictionSet, userGuid);
            } else if (isCasinoRestriction(domainRestrictionSet)) {
                validateCasinoRestrictionPlace(domainRestrictionSet, userGuid);
            }
        }
    }

    private void validateCompsRestrictionPlace(DomainRestrictionSet domainRestrictionSet, String userGuid) throws Status409PlayerRestrictionConflictException, Status403PlayerRestrictionDeniedException {
        String translationKey = "SERVICE-LIMIT.RESTRICTIONS.ERROR_MESSAGES.PROMOTIONS";

        Access access = getUserRestrictions(userGuid);

        //if we have a comps restriction on a player's account we cannot place another comps restriction
        if (!access.isCompsAllowed() && !isDomainRestrictionInterventionCompsBlock(domainRestrictionSet)) {
            translationKey += ".ENABLE_CONFLICT";

            String translatedMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                    domainRestrictionSet.getDomain().getName(), translationKey, translationKey);

            throw new Status409PlayerRestrictionConflictException(translatedMessage);
        }

        if (access.isCompsSystemPlaced() && !isDomainRestrictionInterventionCompsBlock(domainRestrictionSet)) {
            translationKey += ".BLOCKED";

            String translatedMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                    domainRestrictionSet.getDomain().getName(), translationKey, translationKey);
            //Cannot toggle promotion opt when comps is system placed
            throw new Status403PlayerRestrictionDeniedException(translatedMessage);
        }
        //If we have a PLAYER_COMPS_OPTOUT on a player's account, we will continue and add the INTERVENTION_COMPS_BLOCK
    }

    private void validateCasinoRestrictionPlace(DomainRestrictionSet domainRestrictionSet, String userGuid) throws Status409PlayerRestrictionConflictException, Status403PlayerRestrictionDeniedException {
        String translationKey = "SERVICE-LIMIT.RESTRICTIONS.ERROR_MESSAGES.PLAYER_CASINO";

        Access access = getUserRestrictions(userGuid);

        if (!access.isCasinoAllowed()) {
            if (!access.isCasinoSystemPlaced() && isPlayerCasinoBlock(domainRestrictionSet)) {
                UserRestrictionSet userRestrictionSet = find(userGuid, domainRestrictionSet);
                if (userRestrictionSet == null) {
                    return;
                }
                translationKey += ".ENABLE_CONFLICT";

                String translatedMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                        domainRestrictionSet.getDomain().getName(), translationKey, translationKey);

                throw new Status409PlayerRestrictionConflictException(translatedMessage);
            } else if (access.isCasinoSystemPlaced() && isPlayerCasinoBlock(domainRestrictionSet)) {
                translationKey += ".BLOCKED";

                String translatedMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                        domainRestrictionSet.getDomain().getName(), translationKey, translationKey);
                throw new Status403PlayerRestrictionDeniedException(translatedMessage);
            }
        }
    }

    public boolean isPlayerExcludedFromRestriction(DomainRestrictionSet domainRestrictionSet, String userGuid) {

        boolean playerExcluded = false;

        try {
            lithium.service.user.client.objects.User user = getUser(userGuid);
            playerExcluded = playerHasExcludedTagsForRestriction(domainRestrictionSet, user);
        } catch (Status500InternalServerErrorException e) {
            log.error(String.format("Failed while fetching user %s, %s", userGuid, e.getMessage()), e);
        }

        return playerExcluded;
    }

    public boolean playerHasExcludedTagsForRestriction(DomainRestrictionSet set, lithium.service.user.client.objects.User user) {
        if (user.getUserCategories() != null && !user.getUserCategories().isEmpty()) {
            return user.getUserCategories().stream().anyMatch(uc -> uc.getId().equals(set.getExcludeTagId()));
        }
        return false;
    }

    public void validateRestrictionLift(DomainRestrictionSet domainRestrictionSet, String userGuid) throws
            Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException {

        if (domainRestrictionSet.isSystemRestriction() && isCompsRestriction(domainRestrictionSet)) {
            String translationKey = "SERVICE-LIMIT.RESTRICTIONS.ERROR_MESSAGES.PROMOTIONS";

            Access access = getUserRestrictions(userGuid);

            //if the comps is not allowed throw an exception
            if (access.isCompsAllowed()) {
                translationKey += ".DISABLE_CONFLICT";

                String translatedMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                        domainRestrictionSet.getDomain().getName(), translationKey, translationKey);

                throw new Status409PlayerRestrictionConflictException(translatedMessage);
            }

            //if INTERVENTION_COMPS_BLOCK is active, disallow lifting of PLAYER_COMPS_OPTOUT
            if (!isCompsRestrictionOfType(domainRestrictionSet, SystemRestriction.UNDERAGE_COMPS_BLOCK)) {
                if (access.isCompsSystemPlaced() && !isDomainRestrictionInterventionCompsBlock(domainRestrictionSet)) {
                    translationKey += ".BLOCKED";

                    String translatedMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                            domainRestrictionSet.getDomain().getName(), translationKey, translationKey);
                    throw new Status403PlayerRestrictionDeniedException(translatedMessage);
                }
            }

        }
        validateCasinoRestrictionLift(domainRestrictionSet, userGuid);

    }

    private void validateCasinoRestrictionLift(DomainRestrictionSet domainRestrictionSet, String userGuid)
            throws Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException {
        if (domainRestrictionSet.isSystemRestriction() && isCasinoRestriction(domainRestrictionSet)) {
            String translationKey = "SERVICE-LIMIT.RESTRICTIONS.ERROR_MESSAGES.PLAYER_CASINO";

            Access access = getUserRestrictions(userGuid);

            if (access.isCasinoAllowed()) {
                translationKey += ".DISABLE_CONFLICT";

                String translatedMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                        domainRestrictionSet.getDomain().getName(), translationKey, translationKey);

                throw new Status409PlayerRestrictionConflictException(translatedMessage);
            }
            if (access.isCasinoSystemPlaced() && isPlayerCasinoBlock(domainRestrictionSet)) {
                translationKey += ".BLOCKED";
                String translatedMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                        domainRestrictionSet.getDomain().getName(), translationKey, translationKey);
                throw new Status403PlayerRestrictionDeniedException(translatedMessage);
            }
        }
    }

    public boolean isDomainRestrictionInterventionCompsBlock(DomainRestrictionSet domainRestrictionSet) {
        return isCompsRestrictionOfType(domainRestrictionSet, SystemRestriction.INTERVENTION_COMPS_BLOCK);
    }

    public boolean isCompsRestrictionOfType(DomainRestrictionSet domainRestrictionSet, SystemRestriction compsRestriction) {
        if (!isCompsRestriction(domainRestrictionSet)) {
            return false;
        }

        if (!isCompsRestriction(domainRestrictionSet)) {
            return false;
        }

        SystemRestriction restriction = SystemRestriction.findByName(domainRestrictionSet.getName());

        if (restriction != null) {
            return restriction == compsRestriction;
        }

        return false;
    }

    public boolean isCompsRestriction(DomainRestrictionSet domainRestrictionSet) {
        return domainRestrictionSet.getRestrictions()
                .stream()
                .filter(domainRestriction -> domainRestriction.getRestriction().getCode().equalsIgnoreCase(RESTRICTION_COMPS))
                .count() > 0;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<UserRestrictionSet> placeMany(UserRestrictionsRequest userRestrictionsRequest, LithiumTokenUtil util, Map<String, String> failedSet) throws Status500InternalServerErrorException {
        List<UserRestrictionSet> userRestrictionSets = new ArrayList<>();
        List<DomainRestrictionSet> restrictionSets = restrictionService.findByIds(userRestrictionsRequest.getDomainRestrictionSets());

        for (DomainRestrictionSet set : restrictionSets) {
            try {
                userRestrictionSets.add(place(userRestrictionsRequest.getUserGuid(), set, util.guid(), userRestrictionsRequest.getComment(), userRestrictionsRequest.getUserId(), userRestrictionsRequest.getSubType(), util));
            } catch (ErrorCodeException e) {
                log.error("Could not place restriction " + set.getName() + " on user:" + userRestrictionsRequest.getUserGuid(), e);
                failedSet.put(set.getName(), e.getMessage());
            } catch (Exception e) {
                log.error("Could not place restriction " + set.getName() + " on user:" + userRestrictionsRequest.getUserGuid(), e);
            }
        }
        return userRestrictionSets;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<UserRestrictionSet> liftMany(UserRestrictionsRequest userRestrictionsRequest, LithiumTokenUtil util) throws Status500InternalServerErrorException, Status403PlayerRestrictionDeniedException, Status409PlayerRestrictionConflictException {

        List<UserRestrictionSet> userRestrictionSets = new ArrayList<>();
        List<DomainRestrictionSet> restrictionSets = restrictionService.findByIds(userRestrictionsRequest.getDomainRestrictionSets());
        User user = userRepository.findOrCreateByGuid(userRestrictionsRequest.getUserGuid(), () -> new User());

        for (DomainRestrictionSet set : restrictionSets) {
            UserRestrictionSet userRestrictionSet = userRestrictionSetRepository.findByUserAndSet(user, set);


            // Check to see if the user has this restriction,
            // if not, skip.
            if (userRestrictionSet != null) {

                // Splitting out calls to set variables for easier debugging
                String userGuid = userRestrictionsRequest.getUserGuid();
                String utilGuid = util.guid();
                String comment = userRestrictionsRequest.getComment();
                Long userId = userRestrictionsRequest.getUserId();
                DomainRestrictionSet restrictionSet = userRestrictionSet.getSet();

                lift(userGuid, restrictionSet, utilGuid, comment, userId, util);
                userRestrictionSets.add(userRestrictionSet);

            }
        }

        return userRestrictionSets;
    }

    void registerChangeLogsForDomainRestriction(UserRestrictionSet userRestrictionSet, String userGuid, Long userId, String type, LithiumTokenUtil util, String comment) throws Status500InternalServerErrorException {
        for (DomainRestriction restriction : userRestrictionSet.getSet().getRestrictions()) {
            try {
                List<ChangeLogFieldChange> clfc = new ArrayList<>();

                if (type.equalsIgnoreCase("create")) {
                    clfc.add(ChangeLogFieldChange.builder()
                            .field("activeFrom")
                            .fromValue(null)
                            .toValue(userRestrictionSet.getActiveFrom().toString())
                            .build());
                    clfc.add(ChangeLogFieldChange.builder()
                            .field("activeTo")
                            .fromValue(null)
                            .toValue(userRestrictionSet.getActiveTo().toString())
                            .build());
                    clfc.add(ChangeLogFieldChange.builder()
                            .field("createdOn")
                            .fromValue(null)
                            .toValue(userRestrictionSet.getCreatedOn().toString())
                            .build());
                    clfc.add(ChangeLogFieldChange.builder()
                            .field(SubCategory.RESTRICTION.getName())
                            .fromValue(null)
                            .toValue(userRestrictionSet.getSet().getName())
                            .build());
                }

                if (type.equalsIgnoreCase("delete")) {
                    clfc.add(ChangeLogFieldChange.builder()
                            .field(SubCategory.RESTRICTION.getName())
                            .fromValue(userRestrictionSet.getSet().getName())
                            .toValue(null)
                            .build());
                }

                changeLogService.registerChangesForNotesWithFullNameAndDomain("user.restriction", type, userId, util.guid(), util,
                        comment, null, clfc, Category.RESPONSIBLE_GAMING, SubCategory.RESTRICTION, 40, userGuid.substring(0, userGuid.indexOf('/')));

            } catch (Exception ex) {
                String msg = "Note registration for user restriction failed";
                log.error(msg + " [userRestrictionSet=" + userRestrictionSet + "] " + ex.getMessage(), ex);
                throw new Status500InternalServerErrorException(msg);
            }
        }
    }

    public boolean isDomainRestrictionSetUsed(DomainRestrictionSet set) {
        return userRestrictionSetRepository.countBySet(set) > 0;
    }

    public List<UserRestrictionSet> getActiveCompsRestrictions(String userGuid) {
        return getActiveUserRestrictions(userGuid, DateTime.now())
                .stream()
                .filter(sr -> isCompsRestriction(sr.getSet()))
                .collect(Collectors.toList());
    }

    private List<UserRestrictionSet> getActiveCasinoRestrictions(String userGuid) {
        return getActiveUserRestrictions(userGuid, DateTime.now())
                .stream()
                .filter(userRestrictionSet -> isCasinoRestriction(userRestrictionSet.getSet()))
                .collect(Collectors.toList());
    }

    public Boolean playerCasinoBlockExists(String userGuid) {
        return getActiveCasinoRestrictions(userGuid)
                .stream()
                .anyMatch(userRestrictionSet ->
                        SystemRestriction.findByName(userRestrictionSet.getSet().getName()) == SystemRestriction.PLAYER_CASINO_BLOCK
                );
    }

    private boolean isCasinoRestriction(DomainRestrictionSet domainRestrictionSet) {
        return domainRestrictionSet.getRestrictions()
                .stream()
                .anyMatch(domainRestriction -> domainRestriction.getRestriction().getCode().equalsIgnoreCase(RESTRICTION_CASINO));
    }

    public void processUnderAgeCompsEmail(UserRestrictionSet userRestrictionSet) {
        if (userRestrictionSet == null) {
            return;
        }

        DomainRestrictionSet set = userRestrictionSet.getSet();
        SystemRestriction systemRestriction = SystemRestriction.UNDERAGE_COMPS_BLOCK;
        String userGuid = userRestrictionSet.getUser().getGuid();

        if (isCompsRestrictionOfType(set, systemRestriction)) {
            try {
                lithium.service.user.client.objects.User user = userApiInternalClientService.getUserByGuid(userGuid);
                autoRestrictionPlayerCommService.communicateWithPlayer(user, AutoRestrictionRuleSetResult.builder()
                        .user(user)
                        .createdOn(new DateTime(userRestrictionSet.getCreatedOn()))
                        .activeFrom(new DateTime(userRestrictionSet.getActiveFrom()))
                        .activeTo(new DateTime(userRestrictionSet.getActiveTo()))
                        .restrictionSet(set)
                        .build());
            } catch (Exception | UserClientServiceFactoryException e) {
                log.error(String.format("Failed to send email to user:%s after lifting %s", userGuid, systemRestriction), e);
            }
        }
    }

    private void applyPlaceActions(String guid, List<RestrictionOutcomePlaceAction> actions, String comment) throws Status500InternalServerErrorException {
        if (actions.isEmpty()) return;
        List<TransactionProcessingCode> codes = actions.stream()
                .map(RestrictionOutcomePlaceAction::getCode)
                .collect(Collectors.toList());
        proceedCodes(guid, codes, comment);
    }

    private void applyLiftActions(String guid, List<RestrictionOutcomeLiftAction> actions, String comment) throws Status500InternalServerErrorException {
        if (actions.isEmpty()) return;
        List<TransactionProcessingCode> codes = actions.stream()
                .map(RestrictionOutcomeLiftAction::getCode)
                .collect(Collectors.toList());
        proceedCodes(guid, codes, comment);
    }

    private void proceedCodes(String guid, List<TransactionProcessingCode> codes, String comment) throws Status500InternalServerErrorException {
        getCashierClient().proceedCodes(guid, codes, comment);
    }

    private CashierInternalClient getCashierClient() throws Status500InternalServerErrorException {
        try {
            return serviceClientFactory.target(CashierInternalClient.class, "service-cashier", true);
        } catch (LithiumServiceClientFactoryException e) {
            log.error(e.getMessage(), e);
            throw new Status500InternalServerErrorException("Can`t get CashierInternalClient");
        }
    }

    public void checkDomainAllowLiftingPlayerCasinoBlock(String domainName)
            throws Status550ServiceDomainClientException, Status403AccessDeniedException {
        if (!cachingDomainClientService.allowLiftingPlayerCasinoBlock(domainName)) {
            String translationKey = "SERVICE-LIMIT.RESTRICTIONS.ERROR_MESSAGES.PLAYER_CASINO.LIFT_BLOCKED";
            String translatedMessage = RestrictionError.DEFAULT_ERROR_MESSAGE.getResponseMessageLocal(messageSource,
                    domainName, translationKey, translationKey);
            throw new Status403AccessDeniedException(translatedMessage);
        }
    }
}

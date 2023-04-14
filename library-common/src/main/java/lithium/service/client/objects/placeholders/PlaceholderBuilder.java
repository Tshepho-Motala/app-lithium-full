package lithium.service.client.objects.placeholders;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Optional;

import static lithium.service.client.objects.placeholders.SourceService.SERVICE_CASHIER;
import static lithium.service.client.objects.placeholders.SourceService.SERVICE_DOMAIN;
import static lithium.service.client.objects.placeholders.SourceService.SERVICE_LIMIT;
import static lithium.service.client.objects.placeholders.SourceService.SERVICE_USER;
public enum PlaceholderBuilder {
    //service-user
    USER_LAST_NAME("%user.lastName%", SERVICE_USER),
    USER_FIRST_NAME("%user.firstName%", SERVICE_USER),
    USER_NAME("%user.userName%", SERVICE_USER),
    USER_EMAIL_ADDRESS("%user.emailAddress%", SERVICE_USER),
    USER_PENDING_VALIDATION_EMAIL_ADDRESS("%user.pendingValidationEmailAddress%", SERVICE_USER),
    USER_CELLPHONE_NUMBER("%user.cellphoneNumber%", SERVICE_USER),
    USER_VERIFICATION_STATUS("%user.verificationStatus%", SERVICE_USER),
    USER_LAST_NAME_PREFIX("%user.lastNamePrefix%", SERVICE_USER),
    USER_OPT_OUT_EMAIL_URL("%user.optOutEmailUrl%", SERVICE_USER),
    USER_GUID("%user.guid%", SERVICE_USER),
    USER_PLAYER_LINK("%user.playerLink%", SERVICE_USER),
    USER_ACCOUNT_STATUS("%user.accountStatus%", SERVICE_USER),
    USER_AGE_VERIFIED("%user.ageVerified%", SERVICE_USER),
    USER_ADDRESS_VERIFIED("%user.addressVerified%", SERVICE_USER),
    USER_CREATE_DATE("%user.createdDate%", SERVICE_USER),
    USER_DOB("%user.dateOfBirth%", SERVICE_USER),
    USER_RESIDENTIAL_ADDRESS("%user.residentialAddress%"),
    USER_PASSWORD_RESET_URL("%password.reset.url%"),
    USER_PASSWORD_RESET_TOKEN("%password.reset.token%"),
    USER_PASSWORD_RESET_CODE("%password.reset.code%"),
    USER_EMAIL_VALIDATE_URL("%email.validate.url%"),
    USER_VALIDATE_TOKEN("%validate.token%"),
    USER_SMS_VALIDATE_URL("%sms.validate.url%"),
    USER_SMS_VALIDATE_TOKEN("%sms.validate.token%"),

    //service-cashier
    CASHIER_TRANSACTION_TYPE("%cashier.transactionType%", SERVICE_CASHIER),
    CASHIER_TRANSACTION_ID("%cashier.transactionId%", SERVICE_CASHIER),
    CASHIER_AMOUNT("%cashier.amount%", SERVICE_CASHIER),
    CASHIER_PROCESSOR_METHOD("%cashier.processorMethod%", SERVICE_CASHIER),
    CASHIER_PROCESSOR_REFERENCE("%cashier.processorReference%", SERVICE_CASHIER),
    CASHIER_PROCESSOR_RESPONSE("%cashier.processorResponse%"),
    CASHIER_REQUEST("%cashier.request%"),
    CASHIER_RESPONSE("%cashier.response%"),
    CASHIER_BILLING_DESCRIPTOR("%cashier.billingDescriptor%", SERVICE_CASHIER),
    CASHIER_TRANSACTION_FEE("%cashier.transactionFee%", SERVICE_CASHIER),
    CASHIER_NOTIFICATION_METHOD("%cashier.notificationMethod%"),
    CASHIER_PAYMENT_DESCRIPTOR("%cashier.paymentDescriptor%", SERVICE_CASHIER),
    CASHIER_TRANSACTION_CURRENCY_CODE("%cashier.transactionCurrencyCode%", SERVICE_CASHIER),

    //service-limit
    LIMIT_PLAYER_COOLOFF_CREATED_DATE("%playerCoolOff.createdDate%", SERVICE_LIMIT),
    LIMIT_PLAYER_COOLOFF_EXPIRED_DATE("%playerCoolOff.expiryDate%", SERVICE_LIMIT),
    LIMIT_PLAYER_COOLOFF_PERIOD_IN_DAYS("%playerCoolOff.periodInDays%", SERVICE_LIMIT),
    LIMIT_PLAYER_EXCLUSION_CREATED_DATE("%playerExclusion.createdDate%", SERVICE_LIMIT),
    LIMIT_PLAYER_EXCLUSION_EXPIRED_DATE("%playerExclusion.expiryDate%", SERVICE_LIMIT),
    LIMIT_PLAYER_EXCLUSION_DURATION_DAYS("%playerExclusion.durationDays%", SERVICE_LIMIT),
    LIMIT_PLAYER_RESTRICTION_CREATED_DATE("%playerRestriction.createdDate%"),
    LIMIT_PLAYER_RESTRICTION_ACTIVE_TO("%playerRestriction.activeTo%"),
    LIMIT_PLAYER_RESTRICTION_ACTIVE_FROM("%playerRestriction.activeFrom%"),
    LIMIT_PLAYER_RESTRICTION_SUB_TYPE("%playerRestriction.subType%"),
    LIMIT_PLAYER_RESTRICTION_DURATION_DAYS("%playerExclusion.durationDays%"),

    //service-domain
    DOMAIN_NAME("%domain.name%", SERVICE_DOMAIN),
    DOMAIN_URL("%domain.url%", SERVICE_DOMAIN),
    DOMAIN_SUPPORT_EMAIL("%domain.supportEmail%", SERVICE_DOMAIN),

    //service-casino
    CASINO_PLAY_THROUGH_CENTS("%casino.playThroughCents%"),
    CASINO_PLAY_THROUGH_REQUIRED_CENTS("%casino.playThroughRequiredCents%"),
    CASINO_TRIGGER_AMOUNT("%casino.triggerAmount%"),
    CASINO_BONUS_AMOUNT("%casino.bonusAmount%"),
    CASINO_BONUS_PERCENTAGE("%casino.bonusPercentage%"),
    CASINO_BONUS_CODE("%casino.bonusCode%"),
    CASINO_BONUS_NAME("%casino.bonusName%"),

    //service-document
    DOCUMENT_FILE_NAME_1("%document.fileName1%"),
    DOCUMENT_FILE_LINK_1("%document.fileLink1%"),
    DOCUMENT_FILE_TIMESTAMP_1("%document.fileTimestamp1%"),
    DOCUMENT_FILE_NAME_2("%document.fileName2%"),
    DOCUMENT_FILE_LINK_2("%document.fileLink2%"),
    DOCUMENT_FILE_TIMESTAMP_2("%document.fileTimestamp2%"),
    DOCUMENT_TYPE("%document.type%"),
    DOCUMENT_ID("%document.id%"),
    DOCUMENT_ADDRESS("%document.addressExtract%"),

    //service-report
    REPORT_NAME("%report.name%"),
    REPORT_STARTED_ON("%report.startedOn%"),
    REPORT_COMPLETED_ON("%report.completedOn%"),
    REPORT_STARTED_BY("%report.startedBy%"),
    REPORT_TOTAL_RECORDS("%report.totalRecords%"),
    REPORT_PROCESSED_RECORDS("%report.processedRecords%"),
    REPORT_CURRENT_BALANCE("%report.currentBalance%"),
    REPORT_CURRENT_BALANCE_CASINO_BONUS("%report.currentBalanceCasinoBonus%"),
    REPORT_CURRENT_BALANCE_CASINO_BONUS_PENDING("%report.currentBalanceCasinoBonusPending%"),
    REPORT_PERIOD_OPENING_BALANCE("%report.periodOpeningBalance%"),
    REPORT_PERIOD_CLOSING_BALANCE("%report.periodClosingBalance%"),
    REPORT_PERIOD_OPENING_BALANCE_CASINO_BONUS("%report.periodOpeningBalanceCasinoBonus%"),
    REPORT_PERIOD_CLOSING_BALANCE_CASINO_BONUS("%report.periodClosingBalanceCasinoBonus%"),
    REPORT_PERIOD_OPENING_BALANCE_CASINO_BONUS_PENDING("%report.periodOpeningBalanceCasinoBonusPending%"),
    REPORT_PERIOD_CLOSING_BALANCE_CASINO_BONUS_PENDING("%report.periodClosingBalanceCasinoBonusPending%"),
    REPORT_DEPOSIT_AMOUNT("%report.depositAmount%"),
    REPORT_DEPOSIT_COUNT("%report.depositCount%"),
    REPORT_PAYOUT_AMOUNT("%report.payoutAmount%"),
    REPORT_PAYOUT_COUNT("%report.payoutCount%"),
    REPORT_BALANCE_ADJUST_AMOUNT("%report.balanceAdjustAmount%"),
    REPORT_BALANCE_ADJUST_COUNT("%report.balanceAdjustCount%"),
    REPORT_CASINO_BET_AMOUNT("%report.casinoBetAmount%"),
    REPORT_CASINO_BET_COUNT("%report.casinoBetCount%"),
    REPORT_CASINO_WIN_AMOUNT("%report.casinoWinAmount%"),
    REPORT_CASINO_WIN_COUNT("%report.casinoWinCount%"),
    REPORT_CASINO_NET_AMOUNT("%report.casinoNetAmount%"),
    REPORT_CASINO_BONUS_BET_AMOUNT("%report.casinoBonusBetAmount%"),
    REPORT_CASINO_BONUS_BET_COUNT("%report.casinoBonusBetCount%"),
    REPORT_CASINO_BONUS_WIN_AMOUNT("%report.casinoBonusWinAmount%"),
    REPORT_CASINO_BONUS_WIN_COUNT("%report.casinoBonusWinCount%"),
    REPORT_CASINO_BONUS_NET_AMOUNT("%report.casinoBonusNetAmount%"),
    REPORT_CASINO_BONUS_PENDING_AMOUNT("%report.casinoBonusPendingAmount%"),
    REPORT_CASINO_BONUS_TRANSFER_TO_BONUS_PENDING_AMOUNT("%report.casinoBonusTransferToBonusPendingAmount%"),
    REPORT_CASINO_BONUS_TRANSFER_FROM_BONUS_PENDING_AMOUNT("%report.casinoBonusTransferFromBonusPendingAmount%"),
    REPORT_CASINO_BONUS_PENDING_CANCEL_AMOUNT("%report.casinoBonusPendingCancelAmount%"),
    REPORT_CASINO_BONUS_PENDING_COUNT("%report.casinoBonusPendingCount%"),
    REPORT_CASINO_BONUS_ACTIVATE_AMOUNT("%report.casinoBonusActivateAmount%"),
    REPORT_CASINO_BONUS_TRANSFER_TO_BONUS_AMOUNT("%report.casinoBonusTransferToBonusAmount%"),
    REPORT_CASINO_BONUS_TRANSFER_FROM_BONUS_AMOUNT("%report.casinoBonusTransferFromBonusAmount%"),
    REPORT_CASINO_BONUS_CANCEL_AMOUNT("%report.casinoBonusCancelAmount%"),
    REPORT_CASINO_BONUS_EXPIRE_AMOUNT("%report.casinoBonusExpireAmount%"),
    REPORT_CASINO_BONUS_MAX_PAYOUT_EXCESS_AMOUNT("%report.casinoBonusMaxPayoutExcessAmount%"),

    //service-settlement
    REPORT_SETTLEMENT_TO("%settlement.to%"),
    REPORT_SETTLEMENT_PERIOD("%settlement.period%"),

    //service-xp
    XP_PLAYING_TO_LEVEL("%xp.playingToLevel%"),
    XP_PROGRESS("%xp.progress%"),
    XP_BONUS_CODE("%xp.bonusCode%"),
    XP_IS_MILESTONE("%xp.isMilestone%");

    private PlaceholderBuilder(String key, SourceService service) {
        this.key = key;
        this.service = service;
    }

    private PlaceholderBuilder(String key) {
        this.key = key;
        this.service = null;
    }

    @Getter
    @Accessors(fluent = true)
    private String key;

    @Getter
    @Accessors(fluent = true)
    private SourceService service;

    @JsonValue
    public String key() {
        return key;
    }

    @JsonCreator
    public static PlaceholderBuilder fromKey(String key) {
        for (PlaceholderBuilder u : PlaceholderBuilder.values()) {
            if (u.key.equalsIgnoreCase(key)) {
                return u;
            }
        }
        return null;
    }

    public boolean isAutoComplete() {
        return this.service != null;
    }

    public Placeholder from(String value) {
        return new Placeholder(this.key, value);
    }

    public Placeholder from(Optional<String> value) {
        return new Placeholder(this.key, value);
    }

    public Placeholder from(Date date) {
        return new Placeholder(this.key, date);
    }

    public Placeholder from(Long value) {
        return new Placeholder(this.key, value);
    }

    public Placeholder from(Integer value) {
        return new Placeholder(this.key, value);
    }

    public static Placeholder createEmptyPlaceholder(String key) {
        return new Placeholder(key, Optional.empty());
    }
}

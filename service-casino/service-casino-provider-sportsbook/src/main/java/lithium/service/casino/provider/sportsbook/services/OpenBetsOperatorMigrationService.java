package lithium.service.casino.provider.sportsbook.services;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.casino.provider.sportsbook.storage.entities.Bet;
import lithium.service.casino.provider.sportsbook.storage.entities.OpenBetsOpMigrationAudit;
import lithium.service.casino.provider.sportsbook.storage.entities.Reservation;
import lithium.service.casino.provider.sportsbook.storage.entities.ReservationCommit;
import lithium.service.casino.provider.sportsbook.storage.repositories.OpenBetsOpMigrationAuditRepository;
import lithium.service.domain.client.CachingDomainClientService;
import lithium.service.domain.client.DomainSettings;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.domain.client.objects.Domain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class OpenBetsOperatorMigrationService {
    private final CachingDomainClientService cachingDomainClientService;
    private final OpenBetsOpMigrationAuditRepository openBetsOpMigrationAuditRepository;

    @Autowired
    public OpenBetsOperatorMigrationService(CachingDomainClientService cachingDomainClientService,
            OpenBetsOpMigrationAuditRepository openBetsOpMigrationAuditRepository) {
        this.cachingDomainClientService = cachingDomainClientService;
        this.openBetsOpMigrationAuditRepository = openBetsOpMigrationAuditRepository;
    }

    public boolean isOpenBetsOperatorMigrationEnabled(String domainName) {
        try {
            Domain domain = cachingDomainClientService.retrieveDomainFromDomainService(domainName);
            Optional<String> value = domain.findDomainSettingByName(
                    DomainSettings.DANGEROUS_OP_MIGRATION_SB_OPEN_BETS_INGESTION_WITHOUT_BALANCE_ADJUST_ENABLED.key());
            if (value.isPresent()) {
                return Boolean.parseBoolean(value.get());
            } else {
                throw new Status550ServiceDomainClientException("Value for " + DomainSettings
                        .DANGEROUS_OP_MIGRATION_SB_OPEN_BETS_INGESTION_WITHOUT_BALANCE_ADJUST_ENABLED.key()
                        + " is not present");
            }
        } catch (Exception e) {
            log.error("Failed to read state of " +
                    DomainSettings.DANGEROUS_OP_MIGRATION_SB_OPEN_BETS_INGESTION_WITHOUT_BALANCE_ADJUST_ENABLED.key()
                    + " from domain settings, defaulting to "
                    + DomainSettings.DANGEROUS_OP_MIGRATION_SB_OPEN_BETS_INGESTION_WITHOUT_BALANCE_ADJUST_ENABLED
                    .defaultValue() + " [domainName="+domainName+"] " + e.getMessage(), e);
            return Boolean.parseBoolean(DomainSettings
                    .DANGEROUS_OP_MIGRATION_SB_OPEN_BETS_INGESTION_WITHOUT_BALANCE_ADJUST_ENABLED.defaultValue());
        }
    }

    public void validateRequestForReservation(String userGuid) throws Status500InternalServerErrorException {
        boolean allowed = isReservationOrSettlementAllowed(userGuid);
        if (!allowed) {
            throw new Status500InternalServerErrorException("Reservation for this brand is not allowed while open bets"
                    + " migration is running");
        }
    }

    public void validateRequestForSettlement(String userGuid) throws Status500InternalServerErrorException {
        boolean allowed = isReservationOrSettlementAllowed(userGuid);
        if (!allowed) {
            throw new Status500InternalServerErrorException("Settlement for this brand is not allowed while open bets"
                    + " migration is running");
        }
    }

    public void addAuditTrail(Reservation reservation, Bet bet, ReservationCommit reservationCommit) {
        OpenBetsOpMigrationAudit auditTrail = openBetsOpMigrationAuditRepository.save(
                OpenBetsOpMigrationAudit.builder()
                        .createdDate(new Date())
                        .reservation(reservation)
                        .bet(bet)
                        .reservationCommit(reservationCommit)
                        .build());
        log.trace("Added audit trail | {}", auditTrail);
    }

    private boolean isReservationOrSettlementAllowed(String userGuid) throws Status500InternalServerErrorException {
        String[] domainAndUser = userGuid.split("/");
        if (domainAndUser.length != 2) {
            // This should never happen
            throw new Status500InternalServerErrorException("Invalid user");
        }
        String domainName = domainAndUser[0];
        boolean openBetsOpMigrationEnabled = isOpenBetsOperatorMigrationEnabled(domainName);
        log.trace("isReservationOrSettlementAllowed | domainName: {}, openBetsOpMigrationEnabled: {},"
                        + " opAllowed: {}",
                domainName, openBetsOpMigrationEnabled, !openBetsOpMigrationEnabled);
        return !openBetsOpMigrationEnabled;
    }
}

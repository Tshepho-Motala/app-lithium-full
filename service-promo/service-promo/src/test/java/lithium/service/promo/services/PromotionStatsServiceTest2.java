package lithium.service.promo.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lithium.service.promo.data.entities.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
class PromotionStatsServiceTest2 {
  private List<String> blacklisted;
  private List<String> whitelisted;
  private Set<String> exclusivePlayers;
  private boolean isExclusive;
  private String guid = "livescore_uk/586";
  private String guid2 = "livescore_uk/5862";

  @BeforeEach
  void setUp() {
//    isExclusive = true;
    isExclusive = false;
    exclusivePlayers = new HashSet<>();
    exclusivePlayers.add(guid);
//    exclusivePlayers.add(guid2);

    blacklisted = new ArrayList<>();
    whitelisted = new ArrayList<>();

//    blacklisted.add(guid);
//    blacklisted.add(guid2);
//    whitelisted.add(guid);
//    whitelisted.add(guid2);
  }

  @Test
  void exclusivePromoNoTags() {
    if (isExclusive) {
      log.info("exclusive promo, checking blacklist first for {}", guid);
      if (blacklisted.size() > 0) {
        log.warn("blacklist defined, checking {}", guid);
        if (blacklisted.stream().anyMatch(bl -> bl.equalsIgnoreCase(guid))) {
          log.error("{} found in blacklist, stopping.", guid);
          return;
        }
      } else {
        log.debug("blacklist not defined.");
      }
      if (exclusivePlayers.contains(guid)) {
        log.debug("Guid: {} found in exclusive list, adding promo.", guid);
      } else {
        log.warn("Guid: {} NOT found in exclusive list, checking whitelist.", guid);
        if (whitelisted.size() > 0) {
          log.warn("whitelist defined, checking {}", guid);
          if (whitelisted.stream().anyMatch(bl -> bl.equalsIgnoreCase(guid))) {
            log.debug("{} found in whitelist, adding promo.", guid);
          } else {
            log.error("{} NOT found in defined whitelist, stopping.", guid);
            return;
          }
        } else {
          log.warn("No whitelist override defined. promo not added for {}", guid);
        }
      }
    } else {
      log.debug("non exclusive promo, checking {}", guid);

      if (blacklisted.size() > 0) {
        log.warn("blacklist defined, checking {}", guid);
        if (blacklisted.stream().anyMatch(bl -> bl.equalsIgnoreCase(guid))) {
          log.error("{} found in blacklist, stopping.", guid);
          return;
        }
        log.debug("{} not found in blacklist. checking whitelists.", guid);
      } else {
        log.debug("blacklist not defined.");
      }

      if (whitelisted.size() > 0) {
        log.warn("whitelist defined, checking {}", guid);
        if (whitelisted.stream().anyMatch(bl -> bl.equalsIgnoreCase(guid))) {
          log.debug("{} found in whitelist, adding promo.", guid);
        } else {
          log.error("{} NOT found in defined whitelist, stopping.", guid);
          return;
        }
      } else {
        log.warn("No whitelist override defined. adding promo for {}", guid);
      }

    }

//    log.error("Guid: {} NOT allowed to play.", guid);
  }

}
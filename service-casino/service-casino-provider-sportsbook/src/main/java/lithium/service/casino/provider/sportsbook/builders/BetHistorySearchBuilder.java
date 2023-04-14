package lithium.service.casino.provider.sportsbook.builders;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import lithium.service.casino.provider.sportsbook.config.ProviderConfig;
import lithium.service.casino.provider.sportsbook.data.MultipleSelection;
import lithium.service.casino.provider.sportsbook.request.BetHistorySearchRequest;
import lithium.service.casino.provider.sportsbook.request.BetSearchRequest;
import lithium.service.casino.provider.sportsbook.request.EventSearchRequest;
import lithium.service.casino.provider.sportsbook.request.LeagueMarketSportSearchRequest;
import lithium.util.HmacSha256HashCalculator;

/**
 *
 */
public class BetHistorySearchBuilder {

  /**
   *
   * @param preSharedKey
   * @param playerOffset
   * @param brand
   * @param request
   * @return
   */
  public static BetHistorySearchRequest buildBetHistorySearch(String preSharedKey, String playerOffset, String brand, BetHistorySearchRequest request) {
    //Check the bet amount type
    if(request.getBetAmountTypeIn() != null && ! request.getBetAmountTypeIn().isEmpty()) {
      request.setBetAmountType(convertSelection(request.getBetAmountTypeIn()));
    } else {
      request.setBetAmountType(null);
    }

    //Sort out the player offset
    if(request.getCustomerId() != null && ! request.getCustomerId().isEmpty()) {
      request.setCustomerId(generatePlayerOffset(playerOffset, request.getCustomerId()));
    }

    //Format the from date to the correct format
    if(request.getFrom() != null) {
      request.setFrom(convertDate(Boolean.FALSE, request.getFrom()));
    }

    //Check the match type
    if(request.getMatchType() != null && request.getMatchType().isEmpty()) {
      request.setMatchType(null);
    }

    //Format the to date to the correct format
    if(request.getTo() != null) {
      request.setTo(convertDate(Boolean.TRUE, request.getTo()));
    }

    //Get the time stamp
    Long timeStamp = System.currentTimeMillis();

    //Generate the sha256
    request.setSha256(generateSha256(timeStamp, preSharedKey));
    request.setTimestamp(timeStamp);

    //Add the brand
    request.setBrand(brand);

    return request;
  }

  /**
   *
   * @param providerConfig
   * @param inRequest
   * @return
   */
  public static BetSearchRequest buildBetSearch(ProviderConfig providerConfig, BetHistorySearchRequest inRequest) {
    BetSearchRequest request = new BetSearchRequest();

    //Get the bet id.
    request.setBetId(inRequest.getBetId());

    //Get the brand name.
    request.setBrand(providerConfig.getBetSearchBrand());

    //Get the time stamp
    Long timeStamp = System.currentTimeMillis();

    //Generate the sha256
    request.setSha256(generateSha256(timeStamp, providerConfig.getBetSearchKey()));
    request.setTimestamp(timeStamp);

    return request;
  }

  /**
   *
   * @param providerConfig
   * @param request
   * @return
   */
  public static EventSearchRequest buildEventSearch(ProviderConfig providerConfig, EventSearchRequest request) {
    //Check the bet type
    if(request.getLeagues() != null && ! request.getLeagues().isEmpty()) {
      request.setLeagues(request.getLeagues());
    } else {
      request.setLeagues(null);
    }
    request.setBrand(providerConfig.getBetSearchBrand());

    //Get the time stamp
    Long timeStamp = System.currentTimeMillis();

    //Generate the sha256
    request.setSha256(generateSha256(timeStamp, providerConfig.getBetSearchKey()));
    request.setTimestamp(timeStamp);

    return request;
  }

  /**
   *
   * @param providerConfig
   * @param request
   * @return
   */
  public static LeagueMarketSportSearchRequest buildLeagueMarketSportSearch(ProviderConfig providerConfig, LeagueMarketSportSearchRequest request) {
    //Check the bet type
    if(request.getSports() != null && ! request.getSports().isEmpty()) {
      request.setSports(request.getSports());
    } else {
      request.setSports(null);
    }
    request.setBrand(providerConfig.getBetSearchBrand());
    //Get the time stamp
    Long timeStamp = System.currentTimeMillis();

    //Generate the sha256
    request.setSha256(generateSha256(timeStamp, providerConfig.getBetSearchKey()));
    request.setTimestamp(timeStamp);

    return request;
  }

  /**
   *
   * @param timeStamp
   * @param preSharedKey
   * @return
   */
  private static String generateSha256(Long timeStamp, String preSharedKey) {
    HmacSha256HashCalculator hasher = new HmacSha256HashCalculator(preSharedKey);
    hasher.addItem(timeStamp);

    return hasher.calculateHash();
  }

  /**
   *
   * @param playerOffset
   * @param customerId
   * @return
   */
  private static String generatePlayerOffset(String playerOffset, String customerId) {
    if (playerOffset != null && ! playerOffset.isEmpty()) {
      Long customerIDValue = Long.valueOf(customerId);
      Long playerOffsetValue = Long.valueOf(playerOffset);
      Long finalCustomerIdValue = customerIDValue + playerOffsetValue;

      return finalCustomerIdValue.toString();
    }

    return customerId;
  }

  /**
   *
   * @param isEnd
   * @param sourceDateTime
   * @return
   */
  private static String convertDate(Boolean isEnd, String sourceDateTime) {
    //If we have a short form, then just sort out normally
    if(sourceDateTime.length() == 10) {
      if(isEnd) {
        sourceDateTime = sourceDateTime + "T23:59:59.999";
      } else {
        sourceDateTime = sourceDateTime + "T00:00:00.000";
      }

      return sourceDateTime;
    }

    //If it is a long form, then we need to parse it
    DateTimeFormatter sourceFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    DateTimeFormatter targetFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    LocalDateTime dateTime = LocalDateTime.parse(sourceDateTime, sourceFormat);

    return dateTime.atZone(ZoneId.of("UTC")).format(targetFormat);
  }

  /**
   *
   * @param selection
   * @return
   */
  private static String[] convertSelection(List<MultipleSelection> selection) {
    String[] result = new String[selection.size()];

    for(int i = 0; i < result.length; i++) {
      result[i] = selection.get(i).getValue();
    }

    return result;
  }

  private static List<String> convertMultipleSelection(List<MultipleSelection> selection) {
    List<String> result = new ArrayList<>();

    for (MultipleSelection multipleSelection : selection) {
      result.add(multipleSelection.getValue());
    }

    return result;
  }

  public static BetHistorySearchRequest buildSportList(ProviderConfig config) {
    //Get the time stamp
    Long timeStamp = System.currentTimeMillis();

    return BetHistorySearchRequest.builder()
            .brand(config.getBetSearchBrand())
            .sort("id").order("asc").timestamp(timeStamp)
            .sha256(generateSha256(timeStamp, config.getBetSearchKey()))
            .build();
  }

  public static BetHistorySearchRequest buildSportLeagueList(ProviderConfig config) {
    //Get the time stamp
    Long timeStamp = System.currentTimeMillis();

    return BetHistorySearchRequest.builder()
            .brand(config.getBetSearchBrand())
            .sort("id").order("asc").timestamp(timeStamp)
            .sha256(generateSha256(timeStamp, config.getBetSearchKey()))
            .build();
  }
}

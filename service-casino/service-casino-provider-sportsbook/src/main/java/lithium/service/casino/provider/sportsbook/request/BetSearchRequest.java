package lithium.service.casino.provider.sportsbook.request;

import lombok.Data;

/**
 *
 */
@Data
public class BetSearchRequest {

  private String betId;
  private String brand;
  private String sha256;
  private Long timestamp;

  /**
   *
   */
  public BetSearchRequest() {
  }
}

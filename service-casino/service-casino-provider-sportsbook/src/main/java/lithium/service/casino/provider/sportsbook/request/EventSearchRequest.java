package lithium.service.casino.provider.sportsbook.request;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class EventSearchRequest {
  private String brand;
  private List<String> leagues;
  private String sha256;
  private Long timestamp;

  /**
   *
   */
  public EventSearchRequest() {
  }
}

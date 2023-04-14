package lithium.service.reward.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.io.Serializable;

@Data
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class RewardRevisionTypeGame implements Serializable {

  private static final long serialVersionUID = 6011468169419473633L;
  private long id;

  private String guid; // e.g. service-casino-provider-roxor_play-secrets-of-the-phoenix
  private String gameId; //play-secrets-of-the-phoenix
  private String gameName; //Secrets of the phoenix
}

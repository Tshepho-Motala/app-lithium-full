package lithium.service.promo.client.objects;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@JsonIdentityInfo( generator = ObjectIdGenerators.None.class, property = "name" )
public class Label implements Serializable {
  public static final String GAME_GUID = "game_guid";
  public static final String SPORT = "sport";
  public static final String MARKET = "market";
  public static final String LEAGUE = "league";

  private Long id;
  private String name;
}
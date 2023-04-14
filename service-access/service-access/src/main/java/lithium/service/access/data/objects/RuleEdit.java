package lithium.service.access.data.objects;

import java.util.Map;
import lithium.service.access.client.objects.Action;
import lithium.service.access.data.entities.AccessRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RuleEdit {

  private AccessRule ruleset;
  private Long ruleId;
  private Long listId;
  private String type;
  private String providerUrl;
  private Action actionSuccess;
  private Action actionFailed;
  private Integer ipResetTime;
  private Boolean validateOnce;
  private Boolean enabled;
  private String name;
  private Map<String, String> outcomes;
}

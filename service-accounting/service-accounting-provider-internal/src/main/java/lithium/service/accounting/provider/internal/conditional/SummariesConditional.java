package lithium.service.accounting.provider.internal.conditional;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class SummariesConditional extends AllNestedConditions {

  public SummariesConditional() {
    super(ConfigurationPhase.REGISTER_BEAN);
  }

  @ConditionalOnProperty(name = "lithium.is-read-only", havingValue = "false")
  static class ReadOnlyCondition {
  }

  @ConditionalOnProperty(name = "lithium.is-summaries-only", havingValue = "true")
  static class SummariesCondition {
  }
}

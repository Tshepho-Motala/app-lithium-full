package lithium.service.accounting.provider.internal.conditional;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class NotReadOnlyConditional extends AllNestedConditions {

  public NotReadOnlyConditional () {
    super(ConfigurationPhase.REGISTER_BEAN);
  }

  @ConditionalOnProperty(name = "lithium.is-read-only", havingValue = "false")
  static class ReadOnlyCondition {
  }

  @ConditionalOnProperty(name = "lithium.is-summaries-only", havingValue = "false")
  static class SummariesCondition {
  }
}

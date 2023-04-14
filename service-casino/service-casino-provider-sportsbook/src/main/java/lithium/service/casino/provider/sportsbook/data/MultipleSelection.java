package lithium.service.casino.provider.sportsbook.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MultipleSelection {
  private String value;
  private String label;
}

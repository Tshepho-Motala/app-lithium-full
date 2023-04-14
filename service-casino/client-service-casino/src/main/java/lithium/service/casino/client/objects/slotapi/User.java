package lithium.service.casino.client.objects.slotapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class User {
  private long id;
  int version;
  private String guid;
  private Domain domain;
}

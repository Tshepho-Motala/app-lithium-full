package lithium.service.notifications.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class LabelValue implements Serializable {

  private static final long serialVersionUID = 7576866653317867887L;
  private long id;

  private String value;

  private Label label;
}

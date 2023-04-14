package lithium.service.notifications.client.objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString( exclude = "inbox" )
@EqualsAndHashCode( exclude = "inbox" )
public class InboxLabelValue implements Serializable {

  private Long id;
  @JsonBackReference
  private Inbox inbox;
  private Label label;

  private LabelValue labelValue;
}

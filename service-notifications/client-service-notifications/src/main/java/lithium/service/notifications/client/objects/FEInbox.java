package lithium.service.notifications.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class FEInbox {

  private Long inboxId;
  private Date createdDate;
  private String message;
  private String type;
  private Date sentDate;
  private Boolean read;
  private Date readDate;
  private Date lastReadDate;
  private Map<String, String> metaData;
}

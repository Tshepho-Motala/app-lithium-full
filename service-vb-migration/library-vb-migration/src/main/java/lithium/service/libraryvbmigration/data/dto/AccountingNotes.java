package lithium.service.libraryvbmigration.data.dto;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountingNotes {
 private String playerguid;
  private String customerId;
  private String domainName;
  private long entityId;
  private String category;
  private String subCategory;
  private String comments; //note
  private Date creationDate;
  private Date deletionDate;
  private boolean deleted; //isDeleted
}

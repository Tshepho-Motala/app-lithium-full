package lithium.service.changelog.data.context;

import java.util.List;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.changelog.data.entities.Category;
import lithium.service.changelog.data.entities.ChangeLog;
import lithium.service.changelog.data.entities.ChangeLogEntity;
import lithium.service.changelog.data.entities.ChangeLogType;
import lithium.service.changelog.data.entities.Domain;
import lithium.service.changelog.data.entities.SubCategory;
import lithium.service.changelog.data.entities.User;
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
public class ChangelogContext {
  private ChangeLog changeLog;
  private List<ChangeLogFieldChange> changes;

  public ChangeLog changeLog() {
    if (changeLog == null) changeLog = new ChangeLog();
    return changeLog;
  }
  public ChangeLog complete() {
    changeLog = changeLog();
    changeLog.setComplete(true);
    return changeLog;
  }
}

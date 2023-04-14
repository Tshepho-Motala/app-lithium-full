package lithium.service.migration.service.progress;

import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.models.ProgressDto;
import lithium.service.migration.models.enities.Progress;

public interface ProgressTracker {

    float getPercentageProgress(long trackerId);
    Progress recordProgress(Progress progress);
    ProgressDto getProgressForType(MigrationType type);

}

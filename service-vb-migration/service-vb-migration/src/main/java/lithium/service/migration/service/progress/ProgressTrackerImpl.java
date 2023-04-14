package lithium.service.migration.service.progress;

import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.models.ProgressDto;
import lithium.service.migration.models.enities.Progress;
import lithium.service.migration.repo.ProgressRepo;
import lithium.service.migration.util.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
@Slf4j
public class ProgressTrackerImpl implements ProgressTracker{

  @Autowired
  private ProgressRepo progressRepo;

  @Override
  public float getPercentageProgress(long trackerId) {
    Progress progress = progressRepo.findProgressById(trackerId);
    if(progress != null) {
      return  (progress.getLastRowProcessed()/progress.getTotalNumberOfRows()) * 100;
    }
    return  0;

  }

  @Override
  @Transactional
  public Progress recordProgress(Progress progress) {
    if(progress.getTotalNumberOfRows()<=0){
      throw new ApplicationException("Invalid total number of rows: "+ progress.getTotalNumberOfRows());
    }
    if(progress.getLastRowProcessed()<=0){
      throw new ApplicationException("Invalid last row processed: "+ progress.getLastRowProcessed());
    }
    return progressRepo.save(progress);
  }
  @Override
  public ProgressDto getProgressForType(MigrationType type){
    Progress progress = progressRepo.findTopByMigrationTypeOrderByCreatedDateDesc(type);
    log.info("Progress ###############"+progress.toString());
    ProgressDto progressDto = new ProgressDto();
    progressDto.setProcessedRecords(progress.getLastRowProcessed());
    progressDto.setTotalRecords(progress.getTotalNumberOfRows());
    double percentageProgress = ((double) progressDto.getProcessedRecords() / (double)  progressDto.getTotalRecords()) * 100;
    progressDto.setPercentageProgress(percentageProgress);
    log.info("$$$$$$$$$$$$$ percentage {} and progressDto {}", percentageProgress,progressDto);
    return progressDto;
  }
}

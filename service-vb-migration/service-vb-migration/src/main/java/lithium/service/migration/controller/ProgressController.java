package lithium.service.migration.controller;

import lithium.service.libraryvbmigration.data.enums.MigrationType;
import lithium.service.migration.models.ProgressDto;
import lithium.service.migration.service.progress.ProgressTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProgressController {

  @Autowired
  private ProgressTracker progressTracker;

  @GetMapping(value = "progress/{type}")
  ResponseEntity<ProgressDto>  progressAtNow(@PathVariable MigrationType type){
    return new ResponseEntity<>(progressTracker.getProgressForType(type), HttpStatus.OK);
  }


}

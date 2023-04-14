package lithium.service.migration.models;

import lombok.Data;

@Data
public class ProgressDto {

  private double percentageProgress;
  private long totalRecords;
  private long processedRecords;

}

package lithium.service.cdn.provider.google.service.asset.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor(staticName = "ofDescription")
public class BatchProcessingResult {

  @NonNull
  private String description;
  private boolean success;
}


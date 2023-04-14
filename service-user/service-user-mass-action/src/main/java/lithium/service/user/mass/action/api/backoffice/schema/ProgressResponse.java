package lithium.service.user.mass.action.api.backoffice.schema;

import lithium.service.user.mass.action.data.entities.UploadStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProgressResponse {

    private double percentile;
    private UploadStatus uploadStatus;
}

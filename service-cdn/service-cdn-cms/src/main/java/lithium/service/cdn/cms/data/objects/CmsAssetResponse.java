package lithium.service.cdn.cms.data.objects;

import lithium.service.cdn.cms.data.entities.CmsAsset;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CmsAssetResponse {
  private int currentPage;
  private long totalItems;
  private int totalPages;

  private List<CmsAsset> data;
}

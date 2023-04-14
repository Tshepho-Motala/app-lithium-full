package lithium.service.cdn.cms.data.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CmsAssetRequest {
  private int page;
  private int size;
  private String sortBy;
  private String sortOrder;

  Map<String, String> data = new HashMap<>();
}

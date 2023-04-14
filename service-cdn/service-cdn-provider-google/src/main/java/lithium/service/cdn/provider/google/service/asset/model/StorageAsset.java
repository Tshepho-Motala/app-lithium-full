package lithium.service.cdn.provider.google.service.asset.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StorageAsset {
  private String name;
  private String url;
  private long createTime;
  private long size;
}


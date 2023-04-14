package lithium.service.user.client.objects;

import lombok.Builder;

@Builder
public class CollectionDataRevisionEntry {
    public Long id;
    public Long collectionRevisionId;
    public Long collectionDataId;
    public Long lastUpdatedRevisionId;
}

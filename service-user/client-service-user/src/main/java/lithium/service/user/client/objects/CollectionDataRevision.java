package lithium.service.user.client.objects;

import java.util.Date;
import lombok.Builder;

@Builder
public class CollectionDataRevision {
    public Long id;
    public Long userId;
    public Date creationDate;
}

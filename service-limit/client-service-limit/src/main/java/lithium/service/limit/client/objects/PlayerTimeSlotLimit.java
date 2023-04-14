package lithium.service.limit.client.objects;

import lombok.Setter;
import java.io.Serializable;
import java.util.Date;

@Setter
public class PlayerTimeSlotLimit implements Serializable {
    private static final long serialVersionUID = -1;

    private Long id;

    private int version;

    private Domain domain;

    private long limitFromUtc;

    private long limitToUtc;

    private String playerGuid;

    private Date createTimestamp;

    private Date modifyTimestamp = new Date();
}

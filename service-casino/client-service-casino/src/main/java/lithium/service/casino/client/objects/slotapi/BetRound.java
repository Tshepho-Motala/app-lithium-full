package lithium.service.casino.client.objects.slotapi;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BetRound {
    private Long id;
    int version;
    private String guid;
    private boolean complete;
    private Game game;
    private User user;
    private long createdDate;
    private long modifiedDate;
    private int sequenceNumber;
    private BetResult betResult;
}

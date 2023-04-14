package lithium.service.casino.client.objects.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LastBetResultResponse {

    private Date transactionTimestamp;

    private double returns;

    private boolean roundComplete;

    private String betResultKindCode;

}

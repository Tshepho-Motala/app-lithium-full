package lithium.service.casino.provider.roxor.data.response.freegames;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Wins {
    private Prize prize;

    private String timestamp;

    private String screenName;

}
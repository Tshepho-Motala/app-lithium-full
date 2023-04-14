package lithium.service.casino.provider.roxor.data.response.freegames;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GamesAvailability {

    private Summary summary;

    private String gameKey;

    private String status;

    @JsonIgnore
    private User user;

    @JsonIgnore
    private Game game;

    private Date creationDate;

}
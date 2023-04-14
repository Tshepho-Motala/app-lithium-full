package lithium.service.casino.provider.iforium.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {

    @NotNull
    @JsonProperty("ErrorCode")
    private Integer errorCode;
}

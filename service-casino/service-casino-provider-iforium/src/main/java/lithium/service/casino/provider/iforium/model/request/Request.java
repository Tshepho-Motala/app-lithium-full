package lithium.service.casino.provider.iforium.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Request {

    @NotEmpty
    @Size(max = 4)
    @JsonProperty("PlatformKey")
    private String platformKey;

    @NotEmpty
    @Size(max = 50)
    @JsonProperty("Sequence")
    private String sequence;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-ddThh:mm:ss.SSS+00:00")
    @JsonProperty("Timestamp")
    private Date timestamp;
}

package lithium.service.games.provider.google.rge.client.objects.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PredictInstance {
 @JsonProperty("user_guid")
 private String userGuid;
 @JsonProperty("page_size")
 private Integer pageSize;
}

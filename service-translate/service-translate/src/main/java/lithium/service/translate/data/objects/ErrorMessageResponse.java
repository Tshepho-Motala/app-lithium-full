package lithium.service.translate.data.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageResponse {

    Long id;
    String code;
    String description;
    String value;
    List<String> languages;
    String userDefined;
}
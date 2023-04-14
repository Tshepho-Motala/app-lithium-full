package lithium.service.promo.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorResponse {
    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSSZ", timezone = "UTC")
    private Date date;

    @Builder.Default
    private Map<String, List<String>> errors = new HashMap<>();

    public void addError(String fieldName, String error) {

        if (!errors.containsKey(fieldName)) {
            errors.put(fieldName, new ArrayList<>());
        }

        errors.get(fieldName).add(error);
    }
}

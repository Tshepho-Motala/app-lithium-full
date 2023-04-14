package lithium.service.accounting.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@JsonFormat(shape = JsonFormat.Shape.STRING)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ConstraintValidationType {
    REQUIRED
}

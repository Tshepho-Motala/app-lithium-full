package lithium.service.user.provider.sphonic.idin.objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@JsonFormat(shape=JsonFormat.Shape.OBJECT)
@AllArgsConstructor(access= AccessLevel.PRIVATE)
//TODO: Need to be inserted into incomplete_user_status table with id's matching this enum!!

public enum IncompleteUserStatus {
    SUCCESS(1, "Success", "Incomplete user process was successful"),
    FAIL(2, "Fail", "Incomplete user process failed"),
    ERROR(3, "Error", "Server error has occurred during the process"),
    TIMEOUT(4, "Timeout", "Request timed out");

    @Getter
    @Accessors(fluent=true)
    long id;

    @Getter
    @Accessors(fluent=true)
    String statusName;

    @Getter
    @Accessors(fluent=true)
    String description;
}

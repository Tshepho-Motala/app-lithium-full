package lithium.service.user.mass.action.data.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@AllArgsConstructor(access= AccessLevel.PRIVATE)
public enum UploadStatus {
    UPLOADED("When the file has been uploaded and is waiting to be checked."),
    CHECKING("User validation process has started."),
    CHECKED("User validation has been checked."),
    PROCESSING("Mass actions has been initiated and is busy running or waiting to be run."),
    DONE("Mass actions has been completed."),
    FAILED_STAGE_1("Failed during stage 1 - User validations."),
    FAILED_STAGE_2("Failed during stage 2 - Process mass actions.");

    @Getter
    @Accessors(fluent=true)
    private String description;

    @JsonCreator
    public static UploadStatus fromName(String name) {
        for (UploadStatus s: UploadStatus.values()) {
            if (s.name().equalsIgnoreCase(name)) {
                return s;
            }
        }
        return null;
    }
}
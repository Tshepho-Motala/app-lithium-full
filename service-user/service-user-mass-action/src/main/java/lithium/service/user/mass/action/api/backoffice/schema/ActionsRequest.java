package lithium.service.user.mass.action.api.backoffice.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ActionsRequest {

    private Long fileUploadId;

    /**
     * A list of actions that will be performed for a given file upload
     */
    private Set<String> actions;
    private String authorGuid;

    /**
     * Bonus File Upload
     */
    private String bonusCode;
    private Double defaultBonusAmount;
    private String bonusDescription;
    private boolean allowDuplicates;

    /**
     * Player File Upload
     */

    //Status Update
    private String status;
    private String statusReason;
    private String statusComment;

    //Verification Status Update
    private Long verificationStatus;
    private String verificationStatusComment;

    private Boolean ageVerified;

    private Boolean addressVerified;

    //Biometrics Status Update
    private String biometricsStatus;
    private String biometricsStatusComment;

    //Mark as Test Player
    private boolean testPlayer;

    private String accessRule;

    //Update Player Tags
    private Set<Long> addTags;
    private Long replaceTagFrom;
    private Long replaceTagTo;
    private Set<Long> removeTags;

    //Add Note
    private String noteCategory;
    private String noteSubCategory;
    private Integer notePriority;
    private String noteComment;

    //Balance Adjustment
    private Long adjustmentAmountCents;
    private String adjustmentTransactionTypeCode;
    private String adjustmentComment;

    //Update User Restrictions
    private String playerRestrictions;

    public String getAddTagsSetToString() {
        if (this.addTags == null || this.addTags.isEmpty()) { return null; }
        return this.addTags.toString().replace("[", "").replace("]", "");
    }

    public String getRemoveTagsSetToString() {
        if (this.removeTags == null || this.removeTags.isEmpty()) { return null; }
        return this.removeTags.toString().replace("[", "").replace("]", "");
    }
}

package lithium.service.user.mass.action.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lithium.service.user.client.enums.Status;
import lithium.service.user.client.enums.StatusReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@ToString(exclude = {"fileUploadMeta"}) // this was causing stackoverflow on bidirectional relationships
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "mass_action_meta")
public class FileMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column
    private String bonusCode;

    @Column
    private Double defaultBonusAmount;

    @Column
    private String bonusDescription;

    @Column
    private boolean allowDuplicates;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private Status status;

    @Column
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private StatusReason statusReason;

    @Column
    private String statusComment;

    @Column
    private Long verificationStatusId;

    @Column
    private String verificationStatusComment;

    @Column
    private Boolean ageVerified;

    @Column
    private Boolean addressVerified;

    @Column
    private String biometricsStatus;

    @Column
    private String biometricsStatusComment;

    @Column
    private Boolean testPlayer;

    @Column
    private String accessRule;

    @Column
    private String addTags;

    @Column
    private Long replaceTagFrom;

    @Column
    private Long replaceTagTo;

    @Column
    private String removeTags;

    @Column
    private String noteCategory;

    @Column
    private String noteSubCategory;

    @Column
    private Integer notePriority;

    @Column(length = 65535, columnDefinition = "Text")
    private String noteComment;

    @Column
    private Long adjustmentAmountCents;

    @Column
    private String adjustmentTransactionTypeCode;

    @Column
    private String adjustmentComment;

    @OneToOne
    @JsonBackReference
    private FileUpload fileUploadMeta;

    @Column
    private String playerRestrictions;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "action", inverseJoinColumns = {@JoinColumn(name = "mass_action_meta_id")}, joinColumns = {@JoinColumn(name = "id")})
    @JsonManagedReference
    private List<Action> actions = new ArrayList<>();
}

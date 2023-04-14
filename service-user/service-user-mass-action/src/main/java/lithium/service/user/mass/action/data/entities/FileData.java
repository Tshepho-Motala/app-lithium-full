package lithium.service.user.mass.action.data.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lithium.service.user.client.enums.Status;
import lithium.service.user.client.enums.StatusReason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "file_upload_data")
public class FileData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private Long rowNumber;

    @JoinColumn(nullable=false)
    @ManyToOne(fetch= FetchType.EAGER)
    private User player;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = true)
    private boolean duplicate;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private Status userStatus;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    @JsonFormat(shape=JsonFormat.Shape.STRING)
    private StatusReason userStatusReason;

    @Column
    private Boolean ruleSetResultSuccess;

    @Column
    private String ruleSetResultMessage;

    @Column(nullable = true)
    @Enumerated(EnumType.STRING)
    private DataError dataError;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UploadStatus uploadStatus;

    @Column(nullable = true)
    private boolean appliedDefaultAmount;

    @Column(length = 800)
    private String comment;

    public boolean appliedDefaultAmount() {
        return this.appliedDefaultAmount;
    }

    @ManyToOne
    @JsonBackReference
    private FileUpload fileUploadMeta;

    @Column(nullable = false)
    private Long uploadedPlayerId;

    public void clearDataError() {
        this.dataError = null;
    }

    public String getPlayerGuid() {
        if (this.player == null) { return null; }
        return this.player.guid();
    }
}

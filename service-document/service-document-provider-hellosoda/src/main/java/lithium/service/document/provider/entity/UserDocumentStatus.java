package lithium.service.document.provider.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
@Builder(toBuilder = true)
public class UserDocumentStatus {

    @Id
    private String jobId;

    private String status;
    private String userGuid;
    private Long userId;
    private String functionName;
    @Column(length = 100000)
    private String reportBody;
    private String sessionId;
    private String domainName;
    private boolean complete;
    private Long kycVerificationResultId;
}

package lithium.service.user.provider.sphonic.idin.storage.entities;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.*;

import lithium.service.access.provider.sphonic.data.entities.Domain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "idin_request")
public class IDINRequest implements Serializable {
    private static final long serialVersionUID = -97208492760039001L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;
    @Column
    public String lithiumRequestId;
    @Column
    public String sphonicTransactionId;
    @Column
    public String bluemTransactionId;
    @Column
    public String idinApplicantHash;
    @Column
    public String returnUrl;
    @Column
    public String verificationUrl;
    @Column
    public String playerIpAddress;
    @Column
    public Long applicantRefOffset;
    @Version
    public int version;
    @ManyToOne
    @JoinColumn(nullable = false)
    private Domain domain;
    @OneToOne
    @JoinColumn
    private User user;
    @Column
    public Long createdDate;
    @Column
    public Long lastModifiedDate;
}

package lithium.service.limit.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "limit_system_access",
        indexes = {
                @Index(name="idx_domain_limit_id", columnList="domain_name, verification_status_id", unique=true)
        }
)

public class LimitSystemAccess {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "domain_name")
    private String domainName;

    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name = "verification_status_id")
    private VerificationStatus verificationStatus;

    @Column(nullable=false)
    private Boolean login;

    @Column(nullable=false)
    private Boolean deposit;

    @Column(nullable=false)
    private Boolean withdraw;

    @Column(name = "bet_placement", nullable=false)
    private Boolean betPlacement;

    @Column(name = "casino", nullable=false)
    private Boolean casino;
}


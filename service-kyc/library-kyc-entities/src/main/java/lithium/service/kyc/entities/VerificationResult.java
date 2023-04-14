package lithium.service.kyc.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "verification_result"
)
@ToString(exclude= {"vendorsData"})
@JsonIgnoreProperties(ignoreUnknown=true)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class , property = "id")
public class VerificationResult implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = true)
    private String providerRequestId;

    @Column(nullable = false)
    private Date createdOn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private Domain domain;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = true)
    private Provider provider;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false)
    private MethodType methodType;

    @Column(name = "legal_last_name", nullable = true)
    private String legalLastName;

	@Column(name = "full_name", nullable = true)
	private String fullName;

    @Column(name = "date_of_birth",nullable = true)
    private String dob;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private String countryOfBirth;

    @Column(nullable = true)
    private String phoneNumber;

    @Column(nullable = true)
    private String nationality;

    @Column(nullable = true)
    private String methodTypeUid;

	@Column(name = "bvn_uid", nullable = true)
	private String bvnUid;

	@OneToOne
    @JoinColumn(nullable = true)
    private KYCDocument document;

    @OneToOne
    @JoinColumn(nullable = false)
    private ResultMessage resultMessage;

    private boolean success;
    private boolean manual;

	@Column(nullable = true)
    private KYCReason reason;

    @Column(name = "document_decision", nullable = true)
    private String documentDecision;

    @Column(name = "address_decision", nullable = true)
    private String addressDecision;

    @Fetch(FetchMode.SELECT)
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "result", cascade = CascadeType.MERGE)
    private List<VendorData> vendorsData;

	@JsonProperty("reasonValue")
	public String getReasonValue() {
		if (reason != null) {
			return reason.reason();
		}
		return null;
	}
}

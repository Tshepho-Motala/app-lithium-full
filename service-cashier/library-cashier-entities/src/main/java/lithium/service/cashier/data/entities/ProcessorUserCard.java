package lithium.service.cashier.data.entities;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.annotation.JsonView;
import java.util.StringJoiner;
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
import javax.persistence.Transient;
import lithium.service.cashier.data.views.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "cashier.ProcessorUserCard")
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_cashier",
    name = "processor_user_card",
    indexes = {
        @Index(name = "user_reference_idx", columnList = "user_id, reference", unique = true),
        @Index(name = "user_domain_method_processor_idx", columnList = "user_id, domain_method_processor_id", unique = false)
    })
public class ProcessorUserCard {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonView(Views.Internal.class)
  private long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="user_id", nullable=false)
  @JsonView(Views.Internal.class)
  private User user;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "domain_method_processor_id", nullable=false)
  @JsonView(Views.Internal.class)
  private DomainMethodProcessor domainMethodProcessor;

  @Column(name="reference", nullable=false)
  @JsonView(Views.Internal.class)
  private String reference;

  @JsonView(Views.Internal.class)
  private String cardType;
  private String lastFourDigits;
  @JsonView(Views.Internal.class)
  private String bin;
  @JsonView(Views.Internal.class)
  private String expiryDate;
  @JsonView(Views.Internal.class)
  private String scheme;
  @JsonView(Views.Internal.class)
  private String fingerprint;
  @JsonView(Views.Internal.class)
  private String providerData;
  @JsonView(Views.Internal.class)
  private String name;
  @Column(nullable=false)
  @JsonView(Views.Internal.class)
  private Boolean isDefault;
  @ManyToOne
  @JoinColumn(name = "status_id")
  @JsonView(Views.Internal.class)
  private ProcessorAccountStatus status;
  @ManyToOne
  @JoinColumn(name = "type_id")
  @JsonView(Views.Internal.class)
  private ProcessorAccountType type;
  @Column(nullable=false)
  @JsonView(Views.Internal.class)
  private Boolean hideInDeposit;
  @Column(nullable=false)
  @JsonView(Views.Internal.class)
  private Boolean isActive;
  @JsonView(Views.Internal.class)
  private Boolean verified;
  @JsonView(Views.Internal.class)
  private Boolean contraAccount;
  @ManyToOne
  @JoinColumn(name = "failed_verification_id")
  @JsonView(Views.Internal.class)
  private ProcessorAccountVerificationType failedVerification;

  @Transient
  public String getViewName() {
    return domainMethodProcessor.getDomainMethod().getName() + lastFourDigits != null ? "****" + lastFourDigits : "";
  }

  @Transient
  public String getChangeLogFieldName(String fieldName) {
    return "paymentMethod.[" + getViewName() + "]." + fieldName;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", ProcessorUserCard.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("user=" + ofNullable(user)
            .map(u -> "(" + u.getId() + ", " + u.getGuid() + ")")
            .orElse(null))
        .add("domainMethodProcessor=" + ofNullable(domainMethodProcessor)
            .map(dmp -> "(" + dmp.getId() + ", " + dmp.getDescription() + ")")
            .orElse(null))
        .add("reference='" + reference + "'")
        .add("cardType='" + cardType + "'")
        .add("lastFourDigits='" + lastFourDigits + "'")
        .add("bin='" + bin + "'")
        .add("expiryDate='" + expiryDate + "'")
        .add("scheme='" + scheme + "'")
        .add("fingerprint='" + fingerprint + "'")
        .add("providerData='" + providerData + "'")
        .add("name='" + name + "'")
        .add("isDefault=" + isDefault)
        .add("status=" + status)
        .add("type=" + type)
        .add("hideInDeposit=" + hideInDeposit)
        .add("isActive=" + isActive)
        .add("verified=" + verified)
        .add("contraAccount=" + contraAccount)
        .add("failedVerification=" + failedVerification)
        .toString();
  }
}

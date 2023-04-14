package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "user")
@EqualsAndHashCode(exclude = "user")
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(catalog = "lithium_user",
    name = "login_event", indexes = {
    @Index(name = "idx_loginevent_user_id_ip_address_date", columnList = "user_id, ipAddress, date"),
    @Index(name = "idx_loginevent_user_id_successful_date", columnList = "user_id, successful, date"),
    @Index(name = "idx_loginevent_session_key", columnList = "sessionKey"),
    @Index(name = "idx_loginevent_date", columnList = "date"),
    @Index(name = "idx_loginevent_provider_auth_client", columnList = "providerAuthClient"),
    @Index(name = "idx_loginevent_successful_domain_logout_last_activity", columnList = "successful, domain_id, logout, lastActivity"),
    @Index(name = "idx_loginevent_successful_logout_last_activity", columnList = "successful, logout, lastActivity")
})
public class LoginEvent implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(columnDefinition = "TIMESTAMP(3)", insertable = false, updatable = false)
  private Timestamp date;

  @Column(nullable = false)
  private String ipAddress;

  @Column(nullable = true)
  private String country;

  @Column(nullable = true)
  private String countryCode;

  @Column(nullable = true)
  private String state;

  @Column(nullable = true)
  private String city;

  @Column(nullable = true)
  private String os;

  @Column(nullable = true)
  private String browser;

  @Column(nullable = true)
  private String comment;

  @Column(nullable = true, length = 1000)
  private String userAgent;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true)
  @JsonManagedReference("user_loginevent")
  private User user;

  @Column(nullable = false)
  private Boolean successful;

  private Boolean internal;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true)
  private Domain domain;

  @Column(nullable = true)
  private String providerName;

  @Column(nullable = true)
  private String providerUrl;

  @Column(nullable = true)
  private String providerAuthClient;

  @Transient
  @Builder.Default
  private Boolean playerEvent = false;

  @Column
  private Integer errorCode;

  @Column(columnDefinition = "TIMESTAMP(3)")
  private Timestamp logout;

  @Column
  private Long duration;

  @Column
  private String sessionKey;

  @Column
  private Date lastActivity;
}

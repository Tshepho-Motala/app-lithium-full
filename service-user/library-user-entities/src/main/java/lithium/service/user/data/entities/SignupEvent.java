package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(exclude = "user")
@Entity
@Builder
@EqualsAndHashCode(exclude = "user")
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
@Table(catalog = "lithium_user",
    name = "signup_event", indexes = {
    @Index(name = "idx_date", columnList = "date", unique = false),
    @Index(name = "idx_ip_address", columnList = "ipAddress", unique = false),
    @Index(name = "idx_successful", columnList = "successful", unique = false)
})
public class SignupEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private Domain domain;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date date;

  @Column(nullable = false)
  private String ipAddress;

  @Column(nullable = true)
  private String country;

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

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = true)
  private User user;

  @Column(nullable = false)
  private Boolean successful;

  @Column(nullable = true)
  private String userAgent;
}

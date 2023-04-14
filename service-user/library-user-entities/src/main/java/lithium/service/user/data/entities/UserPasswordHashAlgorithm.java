package lithium.service.user.data.entities;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.service.user.converter.EnumConverter.PasswordHashAlgorithmConverter;
import lithium.service.user.enums.PasswordHashAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(catalog = "lithium_user", name = "user_password_hash_algorithm")
public class UserPasswordHashAlgorithm {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  @ManyToOne
  @JoinColumn(nullable = false)
  private User user;

  @Column(nullable = false)
  @Convert(converter = PasswordHashAlgorithmConverter.class)
  private PasswordHashAlgorithm hashAlgorithm;

  @Column(nullable = false)
  private String salt;
}

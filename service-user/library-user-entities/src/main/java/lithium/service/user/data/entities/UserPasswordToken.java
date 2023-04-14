package lithium.service.user.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lithium.service.user.converter.EnumConverter;
import lithium.service.user.enums.Type;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(
    catalog = "lithium_user",
    name = "user_password_token",
    indexes = {
        @Index(name = "idx_upt_createdon", columnList = "createdOn"),
        @Index(name = "idx_u_type", columnList = "user_id, type", unique = true),
        @Index(name = "idx_u_token", columnList = "user_id, token", unique = true)
    }
)
public class UserPasswordToken {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(nullable = false, unique = false)
  private String token;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false)
  private User user;

  @Builder.Default
  @Column(nullable = false)
  private Date createdOn = new Date();

  @Column(nullable = true)
  @Convert(converter = EnumConverter.UserPasswordTokenCommsTypeConverter.class)
  private Type type;

  public UserPasswordToken(String token, User user, Date createdOn, Type type) {
    this.token = token;
    this.user = user;
    this.createdOn = createdOn;
    this.type = type;
  }

  public String getBase64DecodedToken() {
    return new String(java.util.Base64.getDecoder().decode(token), StandardCharsets.UTF_8);
  }
}

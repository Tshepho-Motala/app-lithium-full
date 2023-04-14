package lithium.service.user.data.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Version;
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
@ToString
@EqualsAndHashCode
@Table(
    catalog = "lithium_user",
    name = "user_link"
)
/**
 * Link established between users of a domain or ecosystem. There is a directionality on the linking<br/>
 * The directionality is only applicable in certain link types
 */
public class UserLink implements Serializable {

  private static final long serialVersionUID = -1;
  @JoinColumn
  @ManyToOne
  UserLinkType userLinkType;
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;
  @JoinColumn
  @ManyToOne
  private User primaryUser;
  @JoinColumn
  @ManyToOne
  private User secondaryUser;
  @Column(nullable = true, length = 1000)
  private String linkNote;

  @Builder.Default
  @Column(nullable = false)
  private Boolean deleted = false;

  @Column()
  private Date createdDate;

  @Column()
  private Date updatedDate;

  @PreUpdate
  @PrePersist
  public void calculatedFields() {
    if (createdDate == null)
      createdDate = new Date();
    updatedDate = new Date();
  }
}

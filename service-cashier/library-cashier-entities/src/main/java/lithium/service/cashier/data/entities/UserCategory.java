package lithium.service.cashier.data.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
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
import lithium.service.cashier.data.views.Views;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity(name = "cashier.UserCategories")
@Builder
@EqualsAndHashCode()
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_cashier",
    name = "user_categories",
    indexes = {
        @Index(name = "uidx_user_id_user_category_id", columnList = "user_id, user_category_id", unique = true),
        @Index(name = "idx_user_category_id", columnList = "user_category_id")
    })
public class UserCategory {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @JsonView(Views.Internal.class)
  private long id;

  @ManyToOne(fetch = FetchType.EAGER)
  @JsonBackReference
  @JoinColumn(name="user_id", nullable=false)
  private User user;
  @Column(name="user_category_id",nullable=false)
  @JsonView(Views.Internal.class)
  private long userCategoryId;

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("UserTag{");
    sb.append("id=").append(id);
    sb.append(", userId=").append(user.getId());
    sb.append(", tagId=").append(userCategoryId);
    sb.append('}');
    return sb.toString();
  }
}

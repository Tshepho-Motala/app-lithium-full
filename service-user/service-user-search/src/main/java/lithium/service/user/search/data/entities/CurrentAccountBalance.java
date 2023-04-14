package lithium.service.user.search.data.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "user_search.CurrentAccountBalance")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    catalog = "lithium_user_search",
    name = "current_account_balance",
    indexes = {
        @Index(name = "idx_current_account_balance", columnList = "current_account_balance", unique = false)
    }
)
public class CurrentAccountBalance implements Serializable {

  private static final long serialVersionUID = 167897690L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;

  @Column(nullable = false, name = "current_account_balance")
  private long currentAccountBalance;

  @OneToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column
  private Date timestamp;

}

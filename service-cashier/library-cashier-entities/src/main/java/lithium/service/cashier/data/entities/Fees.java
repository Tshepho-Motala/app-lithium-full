package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Version;
import lithium.math.CurrencyAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@Builder
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(
    catalog = "lithium_cashier",
    name = "fees"
)
public class Fees implements Serializable {

  private static final long serialVersionUID = -72440317557512800L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  private Long flat;
  private BigDecimal percentage;
  private Long minimum;
  private int strategy;


  public BigDecimal getFlatDec() {
    if (flat == null) {
      return null;
    }
    return CurrencyAmount.fromCents(flat).toAmount();
  }

  public void setFlatDec(BigDecimal flatDec) {
    setFlat((flatDec != null) ? CurrencyAmount.fromAmount(flatDec).toCents() : null);
  }

  public BigDecimal getMinimumDec() {
    if (minimum == null) {
      return null;
    }
    return CurrencyAmount.fromCents(minimum).toAmount();
  }

  public void setMinimumDec(BigDecimal minimumDec) {
    setMinimum((minimumDec != null) ? CurrencyAmount.fromAmount(minimumDec).toCents() : null);
  }

  @PrePersist
  private void prePersist() {
    if (flat == null) {
      flat = 0L;
    }
    if (percentage == null) {
      percentage = BigDecimal.ZERO;
    }
    if (minimum == null) {
      minimum = 0L;
    }
    if (strategy < 1) {
      strategy = 1;
    }
  }
}

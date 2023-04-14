package lithium.service.cashier.data.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@JsonIdentityInfo(generator = ObjectIdGenerators.None.class, property = "id")
@Table(
    catalog = "lithium_cashier",
    name = "limits"
)
public class Limits {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Version
  private int version;

  private Long minFirstTransactionAmount;
  private Long maxFirstTransactionAmount;

  private Long minAmount;
  private Long maxAmount;
  private Long maxAmountDay;
  private Long maxAmountWeek;
  private Long maxAmountMonth;

  private Long maxTransactionsDay;
  private Long maxTransactionsWeek;
  private Long maxTransactionsMonth;

  public Long getMinAmount(boolean isFirstTransaction) {
      return isFirstTransaction && minFirstTransactionAmount != null ? minFirstTransactionAmount : minAmount;
  }

  public Long getMaxAmount(boolean isFirstTransaction) {
    return isFirstTransaction && maxFirstTransactionAmount != null ? maxFirstTransactionAmount : maxAmount;
  }

  public BigDecimal getMinAmountDec() {
    if (minAmount == null) {
      return null;
    }
    return new BigDecimal(minAmount).movePointLeft(2);
  }

  public void setMinAmountDec(BigDecimal minAmountDec) {
    setMinAmount((minAmountDec != null) ? minAmountDec.movePointRight(2).longValue() : null);
  }

  public BigDecimal getMinFirstTransactionAmountDec() {
    if (minFirstTransactionAmount == null) {
      return null;
    }
    return new BigDecimal(minFirstTransactionAmount).movePointLeft(2);
  }

  public void setMinFirstTransactionAmountDec(BigDecimal minAmountDec) {
    setMinFirstTransactionAmount((minAmountDec != null) ? minAmountDec.movePointRight(2).longValue() : null);
  }

  public BigDecimal getMaxFirstTransactionAmountDec() {
    if (maxFirstTransactionAmount == null) {
      return null;
    }
    return new BigDecimal(maxFirstTransactionAmount).movePointLeft(2);
  }

  public void setMaxFirstTransactionAmountDec(BigDecimal maxAmountDec) {
    setMaxFirstTransactionAmount((maxAmountDec != null) ? maxAmountDec.movePointRight(2).longValue() : null);
  }

  public BigDecimal getMaxAmountDec() {
    if (maxAmount == null) {
      return null;
    }
    return new BigDecimal(maxAmount).movePointLeft(2);
  }

  public void setMaxAmountDec(BigDecimal maxAmountDec) {
    setMaxAmount((maxAmountDec != null) ? maxAmountDec.movePointRight(2).longValue() : null);
  }

  public BigDecimal getMaxAmountDayDec() {
    if (maxAmountDay == null) {
      return null;
    }
    return new BigDecimal(maxAmountDay).movePointLeft(2);
  }

  public void setMaxAmountDayDec(BigDecimal maxAmountDayDec) {
    setMaxAmountDay((maxAmountDayDec != null) ? maxAmountDayDec.movePointRight(2).longValue() : null);
  }

  public BigDecimal getMaxAmountWeekDec() {
    if (maxAmountWeek == null) {
      return null;
    }
    return new BigDecimal(maxAmountWeek).movePointLeft(2);
  }

  public void setMaxAmountWeekDec(BigDecimal maxAmountWeekDec) {
    setMaxAmountWeek((maxAmountWeekDec != null) ? maxAmountWeekDec.movePointRight(2).longValue() : null);
  }

  public BigDecimal getMaxAmountMonthDec() {
    if (maxAmountMonth == null) {
      return null;
    }
    return new BigDecimal(maxAmountMonth).movePointLeft(2);
  }

  public void setMaxAmountMonthDec(BigDecimal maxAmountMonthDec) {
    setMaxAmountMonth((maxAmountMonthDec != null) ? maxAmountMonthDec.movePointRight(2).longValue() : null);
  }
} 

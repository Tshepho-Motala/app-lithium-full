package lithium.service.cashier.mock.hexopay.data.entities;

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
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "hexopay_card",
    indexes = {
          @Index(name = "stamp_customer_idx", columnList = "stamp,customer_id", unique = true),
          @Index(name = "token_idx", columnList = "token", unique = true),
    }
)
public class CreditCard {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Version
  private int version;

  private String holder;
  private String stamp;
  private String token;
  private String brand;
  @Column(name="last_4")
  private String lastFourDigits;
  @Column(name="first_1")
  private String firstDigit;
  @Column(name="exp_month")
  private Integer expMonth;
  @Column(name="exp_year")
  private Integer expYear;
  private String cvv;
  private String number;
  private boolean secured;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "customer_id")
  private Customer customer;
}

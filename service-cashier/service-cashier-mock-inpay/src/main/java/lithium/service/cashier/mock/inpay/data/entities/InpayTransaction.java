package lithium.service.cashier.mock.inpay.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Version;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "inpay_transaction")
public class InpayTransaction {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private int version;

    @OneToOne
    @JoinColumn(name = "debtor_account_id")
    private InpayDebtorAccount debtorAccount;

    private String endToEndId;

    private String inpayUniqueReference;

    private Long amount;
    private String currency;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date timestamp;

    private String state;

    private String xRequestId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "inpay_transaction_reason", joinColumns = {@JoinColumn(name = "transaction_id")}, inverseJoinColumns = {@JoinColumn(name = "reason_id")})
    @Builder.Default
    private List<InpayReason> reasons = new ArrayList<>();

    public InpayTransaction addReason(InpayReason reason){
        reasons.add(reason);
        return this;
    }
}

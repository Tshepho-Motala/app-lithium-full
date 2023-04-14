package lithium.service.settlement.client.objects;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
public class Settlement implements Serializable {
	private static final long serialVersionUID = 3494257833160928980L;
	
	private Long id;
	private int version;
	private Domain domain;
	private Entity entity;
	private User user;
	private Date createdDate;
	private String createdBy;
	private Date dateStart;
	private Date dateEnd;
	private List<SettlementEntry> settlementEntries;
	private Boolean open;
	private BigDecimal total;
	private SettlementPDF pdf;
	private BatchSettlements batchSettlements;
	private Address physicalAddress;
	private Address billingAddress;
	private BankDetails bankDetails;
}

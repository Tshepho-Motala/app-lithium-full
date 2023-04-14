package lithium.service.report.player.trans.data.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(indexes = {
		@Index(name="idx_pt_crit_id", columnList="query_criteria_id", unique=false),
		@Index(name="idx_pt_tran_id_crit_id", columnList="query_criteria_id, tranId", unique=false),
		@Index(name="idx_pt_tran_entry_date_crit_id", columnList="query_criteria_id, tranEntryDate", unique=false),
		@Index(name="idx_pt_tran_entry_id_crit_id", columnList="query_criteria_id, tranEntryId", unique=true),
		@Index(name="idx_pt_user_guid_crit_id", columnList="query_criteria_id, userGuid", unique=false),
		@Index(name="idx_pt_external_tran_id_crit_id", columnList="query_criteria_id, externalTranId", unique=false),
		@Index(name="idx_pt_tran_entry_amount_crit_id", columnList="query_criteria_id, tranEntryAmount", unique=false),
		@Index(name="idx_pt_user_guid_crit_id", columnList="query_criteria_id, userGuid", unique=false),
		@Index(name="idx_pt_account_type_crit_id", columnList="query_criteria_id, tranEntryAccountType", unique=false),
		@Index(name="idx_pt_account_code_crit_id", columnList="query_criteria_id, tranEntryAccountCode", unique=false),
		@Index(name="idx_pt_provider_guid_crit_id", columnList="query_criteria_id, providerGuid", unique=false),
		@Index(name="idx_pt_game_name_crit_id", columnList="query_criteria_id, gameName", unique=false),
		@Index(name="idx_pt_bonus_code_crit_id", columnList="query_criteria_id, bonusCode", unique=false),
		@Index(name="idx_pt_bonus_name_crit_id", columnList="query_criteria_id, bonusName", unique=false),
		@Index(name="idx_pt_tran_type_crit_id", columnList="query_criteria_id, tranType", unique=false),
		@Index(name="idx_pt_tran_currency_crit_id", columnList="query_criteria_id, tranCurrency", unique=false)
})
public class PlayerTransaction {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;

	@Version
	int version;
	
	private long tranId;
	
	private String tranType;
	
	private String tranCurrency;
	
	private long tranEntryId;
	
	private long tranEntryAmount;
	
	private String tranEntryAccountType;
	
	private String tranEntryAccountCode;
	
	private Date tranEntryDate;
	
	private String userGuid;
	
	@Column(nullable=true)
	private Long tranEntryAccountBalance;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private PlayerTransactionQueryCriteria queryCriteria;
	
	private long bonusRevisionId;
	
	private String bonusName;
	
	private String bonusCode;
	
	private String gameGuid;
	
	private String gameName;
	
	private long playerBonusHistoryId;
	
	private String providerGuid;
	
	private String externalTranId;
	
	private String processingMethod;
	
	private String accountingClientTranId;
	
	private String accountingClientExternalId;

	@Column(nullable = true)
	private String externalTransactionDetailUrl;
}

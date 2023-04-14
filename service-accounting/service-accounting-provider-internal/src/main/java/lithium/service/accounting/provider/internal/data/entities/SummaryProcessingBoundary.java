package lithium.service.accounting.provider.internal.data.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import java.io.Serializable;

@Entity
@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(
		name = "summary_processing_boundary"
)
public class SummaryProcessingBoundary implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static final int DOMAIN_ALL = 1;
//	public static final int DOMAIN_LABEL_VALUE_SUMMARY = 1;
//	public static final int DOMAIN_TRANSACTION_TYPE_SUMMARY = 2;
//	public static final int DOMAIN_SUMMARY = 3;
public static final int DOMAIN_ALL_HISTORICAL = 4;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@Version
	int version;

	@Column(nullable=false)
	private int summaryType;
	
	@Column(nullable=false)
	private long lastTransactionIdProcessed;
	
	@Column(nullable=false)
	private long lastTransactionLabelValueIdProcessed;
}
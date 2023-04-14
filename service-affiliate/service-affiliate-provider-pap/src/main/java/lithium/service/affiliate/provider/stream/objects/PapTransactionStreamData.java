package lithium.service.affiliate.provider.stream.objects;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.Version;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class PapTransactionStreamData implements Serializable {

	private static final long serialVersionUID = 1L;

	private String ownerGuid;
	
	private String transactionType;
	
	private String amount; //currency value with decimal
	
	private String affiliateGuid;
	
	private String bannerGuid;
	
	private String campaignGuid;
	
	private Date transactionDate;
	
	//aux data
	private String authTokenUser;
	
	private String authTokenPassword;
	
	private String referrerUrl;
	
	private String commissionTypeId;
	
	private String baseUrl;
}

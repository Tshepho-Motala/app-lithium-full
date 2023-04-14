package lithium.service.sms.client.objects;

import java.io.Serializable;
import java.util.Date;

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
public class SMS implements Serializable {
	private static final long serialVersionUID = 1401640959223983847L;
	
	private Long id;
	private int version;
	private Date createdDate;
	private Date sentDate;
	private Integer priority;
	private String from;
	private String to;
	private String text;
	private Boolean processing;
	private Integer errorCount;
	private String latestErrorReason;
	private User user;
	private boolean received;
	private String providerReference;
	private DomainProvider domainProvider;
}
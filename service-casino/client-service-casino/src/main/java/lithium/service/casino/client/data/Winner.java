package lithium.service.casino.client.data;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Winner implements Serializable {
	private static final long serialVersionUID = -1L;

	private Long id;
	
	private String domainName;
	
	private Long amount;
	
	private String gameName;
	
	private String userName;
	
	private Date createdDate;
}
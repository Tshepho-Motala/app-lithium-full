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
public class SMSTemplate implements Serializable {
	private static final long serialVersionUID = -7926915071763803208L;
	
	private Long id;
	private int version;
	private String lang;
	private String name;
	private SMSTemplateRevision edit;
	private Date editStartedOn;
	private User editBy;
	private SMSTemplateRevision current;
	private Domain domain;
	private Boolean enabled;
}
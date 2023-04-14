package lithium.service.sms.client.objects;

import java.io.Serializable;

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
public class SMSTemplateRevision implements Serializable {
	private static final long serialVersionUID = 1323212566974168347L;
	
	private Long id;
	private String description;
	private String text;
	private SMSTemplate smsTemplate;
}
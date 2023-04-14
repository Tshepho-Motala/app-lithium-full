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
public class DefaultSMSTemplatePlaceholder implements Serializable {
	private static final long serialVersionUID = 1661614050789909309L;
	
	private Long id;
	private int version;
	private String name;
	private String description;
	private DefaultSMSTemplate defaultSMSTemplate;
}
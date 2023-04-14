package lithium.service.mail.client.objects;

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
public class DefaultEmailTemplatePlaceholder implements Serializable {
	private static final long serialVersionUID = 5815736110417158980L;
	
	private Long id;
	private int version;
	private String name;
	private String description;
	private DefaultEmailTemplate defaultEmailTemplate;
}
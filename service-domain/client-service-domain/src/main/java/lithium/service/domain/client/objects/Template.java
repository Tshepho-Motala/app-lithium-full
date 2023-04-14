package lithium.service.domain.client.objects;

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
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Template implements Serializable {
	private static final long serialVersionUID = -1415354952658363053L;
	private Long id;
	private int version;
	private Domain domain;
	private String name;
	private String lang;
	private Boolean enabled;
	private TemplateRevision current;
	private TemplateRevision edit;
	private Date editStartedOn;
	private User editBy;
}

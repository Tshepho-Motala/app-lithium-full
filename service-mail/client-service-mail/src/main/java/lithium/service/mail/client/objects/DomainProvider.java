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
public class DomainProvider implements Serializable {
	private static final long serialVersionUID = -1636608428683591430L;
	
	private Long id;
	private int version;
	private String name;
	private Provider provider;
	private Domain domain;
	private Boolean enabled;
	private Boolean deleted;
	private Integer priority;
	private String accessRule;
}
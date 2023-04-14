package lithium.service.user.client.objects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Domain implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
	private String displayName;
//	private String description;
//	private Boolean enabled;
//	private Boolean deleted;
//	private String url;
//	private String supportUrl;
//	private String supportEmail;
	private Domain parent;
	private Boolean players;
//	private String signupAccessRule;
//	private String loginAccessRule;
//	private String currency;
}

//Info: The parent and display name fields get populated in LithiumTokenEnhancer class
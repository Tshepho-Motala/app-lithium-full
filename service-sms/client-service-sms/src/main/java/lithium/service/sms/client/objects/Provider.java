package lithium.service.sms.client.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
public class Provider implements Serializable {
	private static final long serialVersionUID = -1462973436003667735L;
	
	private Long id;
	private int version;
	private Boolean enabled;
	private String code;
	private String name;
	private String url;
	private List<ProviderProperty> properties = new ArrayList<>();
}
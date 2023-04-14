package lithium.service.domain.client.objects;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;
import lombok.ToString;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Provider implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private Long id;
	@Builder.Default
	private Integer priority = 1;
	@Builder.Default
	private Boolean enabled = false;
	private String name;
	private String url;
	private Domain domain;
	private ProviderType providerType;
	@Singular
	private List<ProviderProperty> properties;
	
	public boolean internal() {
		if (url.equalsIgnoreCase("internal")) return true;
		return false;
	}
	
	public void providerType(String type) {
		setProviderType(ProviderType.builder().name(type).build());
	}
	
	public String propertyValueOrDefault(String propertyName, String defaultValue) {
		ProviderProperty p = properties.stream().filter(pp -> {
			if (pp.getName().equalsIgnoreCase(propertyName)) {
				return true;
			}
			return false;
		}).findFirst().orElse(null);
		if (p != null) return p.getValue();
		return defaultValue;
	}
	
	public String getPropertyValue(String propertyName) {
		ProviderProperty p = properties.stream().filter(pp -> {
			if (pp.getName().equalsIgnoreCase(propertyName)) {
				return true;
			}
			return false;
		}).findFirst().orElse(null);
		if (p != null) return p.getValue();
		return "";
	}
	
	public Map<String, String> propertyMap() {
		Map<String, String> providerProperties = new HashMap<String, String>();
		if (properties != null) {
			providerProperties = properties.stream().collect(Collectors.toMap(
				p -> getName()+"-"+p.getName(),
				p -> p.getValue()
			));
		}
		providerProperties.put("provider-name", getName());
		return providerProperties;
	}
}
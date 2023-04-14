package lithium.service.casino.provider.twowinpower.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "lithium.service.casino.provider.twowinpower")
public class ConfigurationPropertiesTWP {
	private Boolean loadGamesOnStartup;
	private Boolean downloadImages;
	private String imageDownloadPath;
	private Integer gamePagesToLoad;
	
	public boolean loadGamesOnStartup() {
		return loadGamesOnStartup;
	}
}
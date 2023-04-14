package lithium.service.geo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

import java.util.HashMap;

// The default may not be specified here as it will be used in the @Scheduled annotation that will run
// before this bean is instantiated. Moved to application.yml

@ConfigurationProperties(prefix = "lithium.service.geo")
@Data
public class ServiceGeoConfigurationProperties {

	String maxMindDbFolder;
	Boolean maxMindUpdateEnabled;
	String maxMindUpdateCron;
	String maxMindCityDbUrl;
	String maxMindCityDbPath;
	Long maxMindCityDbAgeBeforeRefresh;
	Long maxMindIspDbAgeBeforeRefresh;
	String maxMindLicenseKey;
	String maxMindIspDbUrl;
	String maxMindIspDbPath;
	String maxMindConnectionTypeDbUrl;
	String maxMindConnectionTypeDbPath;
	Long maxMindConnectionTypeDbAgeBeforeRefresh;
	Boolean geoNamesUpdateEnabled;
	String geoNamesUpdateCron;	
	String geoNamesAllCountriesUrl;
	String geoNamesAllCountriesPath;
	String geoNamesCountryInfoUrl;
	String geoNamesCountryInfoPath;
	String geoNamesAdmin1Url;
	String geoNamesAdmin1Path;
	String geoNamesAdmin2Url;
	String geoNamesAdmin2Path;
	Long geoNamesAgeBeforeRefresh;
	HashMap<String, String> overrideGeoData;
	Integer currentFile = 0;
}

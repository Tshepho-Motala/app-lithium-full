package lithium.service.geo.services;

import java.io.File;
import java.net.InetAddress;
import java.util.List;

import javax.annotation.PostConstruct;

import com.maxmind.geoip2.model.CityResponse;
import lithium.service.geo.data.entities.ConnectionType;
import lithium.service.geo.data.repositories.MaxMindGeoIP2ConnectionTypeRepository;
import lithium.service.geo.data.repositories.MaxMindGeoIp2CityRepository;
import lithium.service.geo.util.DownloadUtil;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import lithium.service.geo.config.ServiceGeoConfigurationProperties;
import lithium.service.geo.data.entities.City;
import lithium.service.geo.data.repositories.CityRepository;
import lithium.service.geo.data.repositories.CountryRepository;
import lithium.service.geo.data.repositories.MaxMindGeoIp2CityRepository;
import lithium.service.geo.objects.Location;
import lithium.service.geo.data.entities.Isp;
import lithium.service.geo.data.repositories.MaxMindGeoIp2IspRepository;
import lithium.service.geo.objects.Network;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MaxMindLookupService {
	@Autowired ApplicationContext appContext;
	@Autowired ServiceGeoConfigurationProperties properties;
	@Autowired CountryRepository countryRepository;
	@Autowired CityRepository cityRepository;

	private MaxMindGeoIp2CityRepository maxMindGeoIp2CityRepository;
	private MaxMindGeoIp2IspRepository maxMindGeoIp2IspRepository;
	private MaxMindGeoIP2ConnectionTypeRepository maxMindGeoIP2ConnectionTypeRepository;

	@PostConstruct
	public void loadInitialDb() throws Exception {
		loadDb(Boolean.TRUE, Boolean.FALSE);
	}

	public void loadDb(Boolean force, Boolean increment) throws Exception {
		//If we are forcing the download or our files are too old, then commence the process
		if(force
				|| DownloadUtil.isTooOld(properties.getMaxMindDbFolder() + properties.getCurrentFile() + "_" + properties.getMaxMindCityDbPath(), properties.getMaxMindCityDbAgeBeforeRefresh())
				|| DownloadUtil.isTooOld(properties.getMaxMindDbFolder() + properties.getCurrentFile() + "_" + properties.getMaxMindIspDbPath(), properties.getMaxMindIspDbAgeBeforeRefresh())
				|| DownloadUtil.isTooOld(properties.getMaxMindDbFolder() + properties.getCurrentFile() + "_" + properties.getMaxMindConnectionTypeDbPath(), properties.getMaxMindConnectionTypeDbAgeBeforeRefresh())) {

			//Increment the current file
			if (increment) {
				properties.setCurrentFile(properties.getCurrentFile() + 1);
			}

			String currentPrefix = properties.getCurrentFile() + "_";

			File maxMindCityDb = loadBaseDb(properties.getMaxMindDbFolder() + currentPrefix + properties.getMaxMindCityDbPath(),
					properties.getMaxMindCityDbUrl()
							.replace("YOUR_LICENSE_KEY", properties.getMaxMindLicenseKey()), force);
			File maxMindIspDb = loadBaseDb(properties.getMaxMindDbFolder() + currentPrefix + properties.getMaxMindIspDbPath(),
					properties.getMaxMindIspDbUrl()
							.replace("YOUR_LICENSE_KEY", properties.getMaxMindLicenseKey()), force);
			File maxMindConnectionTypeDb = loadBaseDb(properties.getMaxMindDbFolder() + currentPrefix + properties.getMaxMindConnectionTypeDbPath(),
					properties.getMaxMindConnectionTypeDbUrl()
							.replace("YOUR_LICENSE_KEY", properties.getMaxMindLicenseKey()), force);

			MaxMindGeoIp2CityRepository maxMindGeoIp2CityRepository = MaxMindGeoIp2CityRepository.getSingletonInstance(maxMindCityDb);
			MaxMindGeoIp2IspRepository maxMindGeoIp2IspRepository = MaxMindGeoIp2IspRepository.getSingletonInstance(maxMindIspDb);
			MaxMindGeoIP2ConnectionTypeRepository maxMindGeoIP2ConnectionTypeRepository = MaxMindGeoIP2ConnectionTypeRepository
					.getSingletonInstance(maxMindConnectionTypeDb);

			log.debug("Testing MaxMindCityDb lookup service: " + maxMindGeoIp2CityRepository.getCityResponse(InetAddress.getLocalHost().getHostAddress()));
			this.maxMindGeoIp2CityRepository = maxMindGeoIp2CityRepository;

			log.debug("Testing MaxMindIspDb lookup service: " + maxMindGeoIp2IspRepository.getIspByAddress(InetAddress.getLocalHost().getHostAddress()));
			this.maxMindGeoIp2IspRepository = maxMindGeoIp2IspRepository;

			log.debug("Testing MaxMindGeoIP2ConnectionTypeDb lookup service: " + maxMindGeoIP2ConnectionTypeRepository
					.findConnectionTypeByAddress(InetAddress.getLocalHost().getHostAddress()));
			this.maxMindGeoIP2ConnectionTypeRepository = maxMindGeoIP2ConnectionTypeRepository;

			//Delete the previous files if we have an increment
			if (increment) {
				Integer old = properties.getCurrentFile() - 1;
				String oldPrefix = old + "_";
				DownloadUtil.cleanUp(properties.getMaxMindDbFolder() + oldPrefix + properties.getMaxMindCityDbPath());
				DownloadUtil.cleanUp(properties.getMaxMindDbFolder() + oldPrefix + properties.getMaxMindIspDbPath());
				DownloadUtil.cleanUp(properties.getMaxMindDbFolder() + oldPrefix + properties.getMaxMindConnectionTypeDbPath());
			}
		}
	}

	public File loadBaseDb(String maxMindDbPath, String maxMindUrlPath, Boolean force) throws Exception {
		File db = new File(maxMindDbPath);

		if (!db.exists()) {
			log.info("No maxmind db yet at " + db + ". Writing packaged DB to " + db);

			DownloadUtil.download(maxMindUrlPath, maxMindDbPath, DownloadUtil.COMPRESSION_TYPE_TAR_GZIP);
			db = new File(maxMindDbPath);
		}

		return db;
	}

	public Location lookup(String address) {

		if (maxMindGeoIp2CityRepository == null) return null;

		CityResponse cityResponse = maxMindGeoIp2CityRepository.getCityResponse(address);

		if (cityResponse == null) return null;

		Location location = new Location();
		location.setCountry(countryRepository.findByCode(cityResponse.getCountry().getIsoCode()));
		List<City> cities = cityRepository.findByCountryCodeAndName(cityResponse.getCountry().getIsoCode(), cityResponse.getCity().getName());
		City city = null;
		//TODO we need to do some more heroistics should there be more than one entry returned here to
		// compare against regions etc.
		if (cities.size() > 0) {
			city = cities.get(0);
		}
		if (city != null) {
			location.setLevel1(city.getLevel1());
			location.setLevel2(city.getLevel2());
			location.setCity(city);
		}
		location.setLatitude(cityResponse.getLocation().getLatitude());
		location.setLongitude(cityResponse.getLocation().getLongitude());

		ConnectionType connectionType = maxMindGeoIP2ConnectionTypeRepository.findConnectionTypeByAddress(address);
		Isp isp = maxMindGeoIp2IspRepository.getIspByAddress(address);
		Network network = Network.builder().isp(isp).connectionType(connectionType).build();

        location.setNetwork(network);
		
		return location;
	}

}

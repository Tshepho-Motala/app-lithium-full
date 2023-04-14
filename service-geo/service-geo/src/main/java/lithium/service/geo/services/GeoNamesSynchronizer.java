package lithium.service.geo.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import lithium.client.changelog.ChangeMapper;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.leader.LeaderCandidate;
import lithium.metrics.LithiumMetricsService;
import lithium.service.Counter;
import lithium.service.geo.config.ServiceGeoConfigurationProperties;
import lithium.service.geo.data.entities.AdminLevel1;
import lithium.service.geo.data.entities.AdminLevel2;
import lithium.service.geo.data.entities.City;
import lithium.service.geo.data.entities.Country;
import lithium.service.geo.data.repositories.AdminLevel1Repository;
import lithium.service.geo.data.repositories.AdminLevel2Repository;
import lithium.service.geo.data.repositories.CityRepository;
import lithium.service.geo.data.repositories.CountryRepository;
import lithium.service.geo.util.DownloadUtil;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GeoNamesSynchronizer {

	public static final String COUNTRY_LABEL = "country";
	public static final String ADMIN1_LABEL = "admin1";
	public static final String ADMIN2_LABEL = "admin2";
	public static final String CITY_LABEL = "city";

	@Autowired LeaderCandidate candidate;
	@Autowired ServiceGeoConfigurationProperties properties;
	@Autowired CountryRepository countryRepository;
	@Autowired AdminLevel1Repository adminLevel1Repository;
	@Autowired AdminLevel2Repository adminLevel2Repository;
	@Autowired CityRepository cityRepository;
	@Autowired FileUpdateService fileUpdateService;
	@Autowired LithiumMetricsService metrics;

	@Value("${lithium.service.geo.services.min-city-population:100}")
	long minCityPopulation;
	
	public void retrieveDatabase() throws Exception {

		if(DownloadUtil.isTooOld(properties.getGeoNamesCountryInfoPath(), properties.getGeoNamesAgeBeforeRefresh())) {
			DownloadUtil.download(properties.getGeoNamesCountryInfoUrl(),
					properties.getGeoNamesCountryInfoPath(),
					DownloadUtil.COMPRESSION_TYPE_NONE);
		}
		if(DownloadUtil.isTooOld(properties.getGeoNamesAdmin1Path(), properties.getGeoNamesAgeBeforeRefresh())) {
			DownloadUtil.download(properties.getGeoNamesAdmin1Url(),
					properties.getGeoNamesAdmin1Path(),
					DownloadUtil.COMPRESSION_TYPE_NONE);
		}

		if(DownloadUtil.isTooOld(properties.getGeoNamesAdmin2Path(), properties.getGeoNamesAgeBeforeRefresh())) {
			DownloadUtil.download(properties.getGeoNamesAdmin2Url(),
					properties.getGeoNamesAdmin2Path(),
					DownloadUtil.COMPRESSION_TYPE_NONE);
		}

		if(DownloadUtil.isTooOld(properties.getGeoNamesAllCountriesPath(), properties.getGeoNamesAgeBeforeRefresh())) {
			DownloadUtil.download(properties.getGeoNamesAllCountriesUrl(),
					properties.getGeoNamesAllCountriesPath(),
					DownloadUtil.COMPRESSION_TYPE_ZIP);
		}

	}
	
	/**
	 * 
	 * http://download.geonames.org/export/dump/admin1CodesASCII.txt
	 * 
	 * See http://download.geonames.org/export/dump/readme.txt
	 * 
	 * The main 'geoname' table has the following fields :
	 * ---------------------------------------------------
	 * geonameid         : integer id of record in geonames database
	 * name              : name of geographical point (utf8) varchar(200)
	 * asciiname         : name of geographical point in plain ascii characters, varchar(200)
	 * alternatenames    : alternatenames, comma separated, ascii names automatically transliterated, convenience attribute from alternatename table, varchar(10000)
	 * latitude          : latitude in decimal degrees (wgs84)
	 * longitude         : longitude in decimal degrees (wgs84)
	 * feature class     : see http://www.geonames.org/export/codes.html, char(1)
	 * feature code      : see http://www.geonames.org/export/codes.html, varchar(10)
	 * country code      : ISO-3166 2-letter country code, 2 characters
	 * cc2               : alternate country codes, comma separated, ISO-3166 2-letter country code, 200 characters
	 * admin1 code       : fipscode (subject to change to iso code), see exceptions below, see file admin1Codes.txt for display names of this code; varchar(20)
	 * admin2 code       : code for the second administrative division, a county in the US, see file admin2Codes.txt; varchar(80) 
	 * admin3 code       : code for third level administrative division, varchar(20)
	 * admin4 code       : code for fourth level administrative division, varchar(20)
	 * population        : bigint (8 byte int) 
	 * elevation         : in meters, integer
	 * dem               : digital elevation model, srtm3 or gtopo30, average elevation of 3''x3'' (ca 90mx90m) or 30''x30'' (ca 900mx900m) area in meters, integer. srtm processed by cgiar/ciat.
	 * timezone          : the iana timezone id (see file timeZone.txt) varchar(40)
	 * modification date : date of last modification in yyyy-MM-dd format
	 */
	public void populateCities() throws Exception {
		final Counter count = new Counter();
		
		File db = new File(properties.getGeoNamesAllCountriesPath());
		if (!fileUpdateService.hasChanged("geonamescities", db)) return;
		log.debug("Populating from " + db);
		
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
		
		String[] columns = {
				"geonameid", "name", "asciiName", "alternateNames", "latitude", "longitude", "featureClass", "featureCode",
				"countryCode", "cc2", "admin1", "admin2", "admin3", "admin4", "population", "elevation", "dem", "timeZone", 
				"modificationDate" };

		String[] fields = { "code", "name", "country", "level1", "level2", "latitude", "longitude", "population" };

		tokenizer.setNames(columns);
		tokenizer.setQuoteCharacter((char) 0);
		
		BufferedReader reader = new BufferedReader(new FileReader(db));
		try {
			String line = null;
			int lineNr = 0;
			
			do {

				lineNr ++;
				line = reader.readLine();
				
				if (line == null) continue;
				if (line.startsWith("#")) continue;
				
				try {
					FieldSet token = tokenizer.tokenize(line);
					
					String name = token.readString("name");
					String code = token.readString("geonameid");
					String countryCode = token.readString("countryCode");
					String admin1 = token.readString("admin1");
					String admin2 = token.readString("admin2");
					Long population = token.readLong("population", 0L);
					Double latitude = token.readDouble("latitude");
					Double longitude = token.readDouble("longitude");
					
					String admin1Code = countryCode + "." + admin1;
					String admin2Code = admin1Code + "." + admin2;
					
					String featureClass = token.readString("featureClass");
					if (!featureClass.equals("P")) continue;
					
					if (population < minCityPopulation) continue;
					
					City texisting = cityRepository.findByCode(code);
					
					if (texisting != null) {
						if (texisting.getManual()) continue;
					} else {
						texisting = new City();
					}
					
					final City existing = texisting; 

					metrics.timer(log).time("populateCity", (StopWatch sw) -> {

						City city = new City();
						city.setCode(code);
						city.setName(name);
						city.setPopulation(population);
						city.setLatitude(latitude);
						city.setLongitude(longitude);

						if ((existing.getCountry() == null) || (!existing.getCountry().getCode().equals(countryCode))) {
							if(countryCode != null && countryCode.length() > 0) {
								city.setCountry(countryRepository.findByCode(countryCode));
							}
						} else {
							city.setCountry(existing.getCountry());
						}
	
						if ((existing.getLevel1() == null) || (!existing.getLevel1().getCode().equals(admin1Code))) {
							if(admin1 != null && admin1.length() > 0) {
								city.setLevel1(adminLevel1Repository.findByCode(admin1Code));
							}
						} else {
							city.setLevel1(existing.getLevel1());
						}
	
						if ((existing.getLevel2() == null) || (!existing.getLevel2().getCode().equals(admin2Code))) {
							if ((admin2 != null) && (admin2.length() > 0)) {
								AdminLevel2 level2 = adminLevel2Repository.findByCode(admin2Code);
								city.setLevel2(level2);
							}
						} else {
							city.setLevel2(existing.getLevel2());
						}
	
						log.debug("City identified: " + city);
						
						List<ChangeLogFieldChange> changes = ChangeMapper.copy(city, existing, fields);
						
						if (!changes.isEmpty()) {
							City cty = cityRepository.save(existing);
							if(cty.getCountry() == null) {
								throw new Exception("city has no country: " + cty.getCode() + " "+cty.getName());
							}
							log.debug("Registered city " + existing);
							log.debug("Changes " + changes);
							count.increment();
						}

					});

				} catch (Exception e) {
					log.error("Error on line " + lineNr + ": " + line, e);
					try { 
						tokenizer.tokenize(line); 
					} catch (Exception ex) {};
					throw new RuntimeException("City error");
				}
				
			} while (line != null);
		} finally { reader.close(); };

		if (count.getValue() > 0) log.info(count + " cities updated / inserted");
	}
	
	public void populateCountries() throws Exception {
		
		final Counter count = new Counter();

		File db = new File(properties.getGeoNamesCountryInfoPath());
		if (!fileUpdateService.hasChanged("geonamescountries", db)) return;
		log.debug("Populating from " + db);
		
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
		
		String[] columns = {
				"code", "iso3", "isoNr", "fips", "name", "capital", "sqkm", "population", "continent", "topLevelDomain", "currencyCode",
				"currencyName", "phone", "postalCodeFormat", "postalCodeRegex", "languages", "geoNameId", "neighbours", "equivalentFips" };

		String[] fields = {
				"code", "iso3", "isoNr", "fips", "name", "capital", "sqkm", "population", "continent", "topLevelDomain", "currencyCode",
				"currencyName", "phone", "postalCodeFormat", "postalCodeRegex", "languages", "neighbours", "equivalentFips" };

		tokenizer.setNames(columns);
		tokenizer.setStrict(true);
		
		BufferedReader reader = new BufferedReader(new FileReader(db));
		try {
			String line = null;
			int lineNr = 0;
			
			do {

				lineNr ++;
				line = reader.readLine();
				
				if (line == null) continue;
				if (line.startsWith("#")) continue;
				
				try {
					FieldSet token = tokenizer.tokenize(line);
					
					String code = token.readString("code");
					if (code == null) continue; 
					
					Country existingCountry = countryRepository.findByCode(code);
					
					if (existingCountry != null) {
						if (existingCountry.getManual()) continue;
					} else {
						existingCountry = new Country();
					}
						
					Country country = new Country();
					country.setCode(code);
					
					country.setIso3(token.readString("iso3"));
					country.setIsoNr(token.readInt("isoNr"));
					country.setFips(token.readString("fips"));
					country.setName(token.readString("name"));
					country.setCapital(token.readString("capital"));
					country.setSqkm(token.readLong("sqkm"));
					country.setPopulation(token.readLong("population"));
					country.setContinent(token.readString("continent"));
					country.setTopLevelDomain(token.readString("topLevelDomain"));
					country.setCurrencyCode(token.readString("currencyCode"));
					country.setCurrencyName(token.readString("currencyName"));
					country.setPhone(token.readString("phone"));
					country.setPostalCodeFormat(token.readString("postalCodeFormat"));
					country.setPostalCodeRegex(token.readString("postalCodeRegex"));
					country.setLanguages(token.readString("languages"));
					country.setNeighbours(token.readString("neighbours"));
					country.setEquivalentFips(token.readString("equivalentFips"));
					
					if ((country.getFips() != null) && (country.getFips().trim().length() == 0)) country.setFips(null);
					log.debug("Country identified: " + country);
					
					List<ChangeLogFieldChange> changes = ChangeMapper.copy(country, existingCountry, fields);
					
					if (!changes.isEmpty()) {
						countryRepository.save(existingCountry);
						log.debug("Registered country " + existingCountry);
						log.debug("Changes " + changes);
						count.increment();
					}
					
				} catch (Exception e) {
					log.error("Error on line " + lineNr + ": " + line, e);
					throw new RuntimeException("country file is broken");
				}
				
			} while (line != null);
		} finally { reader.close(); };
		
		if (count.getValue() > 0) log.info(count + " countries updated / inserted");

	}
	
	public void populateAdminLevel1() throws Exception {
		
		final Counter count = new Counter();

		File db = new File(properties.getGeoNamesAdmin1Path());
		if (!fileUpdateService.hasChanged("geonamesadmin1", db)) return;
		
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
		
		String[] columns = { "code", "name" };

		tokenizer.setNames(columns);
		tokenizer.setStrict(false);
		
		BufferedReader reader = new BufferedReader(new FileReader(db));
		try {
			String line = null;
			int lineNr = 0;
			
			do {

				lineNr ++;
				line = reader.readLine();
				
				if (line == null) continue;
				if (line.startsWith("#")) continue;
				
				try {
					FieldSet token = tokenizer.tokenize(line);
					
					String code = token.readString("code");
					if (code == null) continue; 
					
					AdminLevel1 existing = adminLevel1Repository.findByCode(code);
					
					if (existing != null) {
						if (existing.getManual()) continue;
					} else {
						existing = new AdminLevel1();
					}
						
					AdminLevel1 level = new AdminLevel1();
					level.setCode(code);
					level.setName(token.readString("name"));
					log.debug("Level identified: " + level);
					
					List<ChangeLogFieldChange> changes = ChangeMapper.copy(level, existing, columns);
					
					if (!changes.isEmpty()) {
						if (existing.getCountry() == null) {
							String countryCode = code.split("\\.")[0];
							existing.setCountry(countryRepository.findByCode(countryCode));
						}
						adminLevel1Repository.save(existing);
						log.debug("Registered level " + existing);
						log.debug("Changes " + changes);
						count.increment();
					}
					
				} catch (Exception e) {
					log.error("Error on line " + lineNr + ": " + line, e);
					throw new RuntimeException("problem in level1 file");
				}
				
			} while (line != null);
		} finally { reader.close(); };
		if (count.getValue() > 0) log.info(count + " level 1 areas updated / inserted");
	}

	public void populateAdminLevel2() throws Exception {
		
		final Counter count = new Counter();
		
		File db = new File(properties.getGeoNamesAdmin2Path());
		if (!fileUpdateService.hasChanged("geonamesadmin2", db)) return;
		
		DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
		
		String[] columns = { "code", "name" };

		tokenizer.setNames(columns);
		tokenizer.setStrict(false);
		
		BufferedReader reader = new BufferedReader(new FileReader(db));
		try {
			String line = null;
			int lineNr = 0;
			
			do {

				lineNr ++;
				line = reader.readLine();
				
				if (line == null) continue;
				if (line.startsWith("#")) continue;
				
				try {
					FieldSet token = tokenizer.tokenize(line);
					
					String code = token.readString("code");
					if (code == null) continue; 
					
					AdminLevel2 existing = adminLevel2Repository.findByCode(code);
					
					if (existing != null) {
						if (existing.getManual()) continue;
					} else {
						existing = new AdminLevel2();
					}
						
					AdminLevel2 level = new AdminLevel2();
					level.setCode(code);
					level.setName(token.readString("name"));
					log.debug("Level 2 identified: " + level);
					
					List<ChangeLogFieldChange> changes = ChangeMapper.copy(level, existing, columns);
					
					if (!changes.isEmpty()) {
						
						if (existing.getLevel1() == null) {
							String countryCode = code.split("\\.")[0];
							String level1Code = code.split("\\.")[1];
							existing.setLevel1(adminLevel1Repository.findByCode(countryCode + "." + level1Code));
						}
						
						adminLevel2Repository.save(existing);
						log.debug("Registered level 2 " + level);
						log.debug("Changes " + changes);
						count.increment();
					}
					
				} catch (Exception e) {
					log.error("Error on line " + lineNr + ": " + line, e);
					try { 
						tokenizer.tokenize(line); 
					} catch (Exception ex) {};
					throw new RuntimeException("problem in level2 file");
				}
				
			} while (line != null);
		} finally { reader.close(); };
		if (count.getValue() > 0) log.info(count + " level 2 areas updated / inserted");
	}

	@Scheduled(cron="${lithium.service.geo.geo-names-update-cron}") 
	public void download() throws Exception {
		if (!properties.getGeoNamesUpdateEnabled()) return;
		if (!candidate.iAmTheLeader()) return;
		retrieveDatabase();
		populateCountries();
		populateAdminLevel1();
		populateAdminLevel2();
		populateCities();
		overrideFromConfig();
	}

	/**
	 * Reads from lithium.service.geo.override-geo-data a key-value map.
	 * Format is country|admin1|admin2|city followed by | and then the code for the relevant geo table.
	 * Eg. { "country|ZA" : "souff africka" } will replace the name for the country.
	 *
	 * Should it become a requirement in future, the replacement field can also be defined as an additional | in the key but that is beyond the scope of this change.
	 * Also it would be nice to add new ones and remove existing ones.
	 */
	private void overrideFromConfig() {
		if (properties.getOverrideGeoData() != null && !properties.getOverrideGeoData().isEmpty()) {
			properties.getOverrideGeoData().forEach((k, v) -> {
				String[] keySplit = k.split("\\|");
				if (keySplit.length < 2) {
					log.warn("Incorrect geo data override key used. " + k + " Please start the key with country|admin1|admin2|city followed by | and then the relevant code eg. country|ZA");
				}
				switch (keySplit[0]) {
					case COUNTRY_LABEL: {
						Country country = countryRepository.findByCode(keySplit[1]);
						if (country != null || !country.getName().contentEquals(v)) {
							country.setName(v);
							country.setManual(true);
							countryRepository.save(country);
							log.info("Success overriding country name: " + country);
						}
					}
					break;
					case ADMIN1_LABEL: {
						AdminLevel1 adminLevel1 = adminLevel1Repository.findByCode(keySplit[1]);
						if (adminLevel1 != null || !adminLevel1.getName().contentEquals(v)) {
							adminLevel1.setName(v);
							adminLevel1.setManual(true);
							adminLevel1Repository.save(adminLevel1);
							log.info("Success overriding admin1 name: " + adminLevel1);
						}
					}
					break;
					case ADMIN2_LABEL: {
						AdminLevel2 adminLevel2 = adminLevel2Repository.findByCode(keySplit[1]);
						if (adminLevel2 != null || !adminLevel2.getName().contentEquals(v)) {
							adminLevel2.setName(v);
							adminLevel2.setManual(true);
							adminLevel2Repository.save(adminLevel2);
							log.info("Success overriding admin2 name: " + adminLevel2);
						}
					}
					break;
					case CITY_LABEL: {
						City city = cityRepository.findByCode(keySplit[1]);
						if (city != null || !city.getName().contentEquals(v)) {
							city.setName(v);
							city.setManual(true);
							cityRepository.save(city);
							log.info("Success overriding city name: " + city);
						}
					}
					break;
					default: {
						log.warn("Could not use provided key for geo override: " + k);
					}
				}
			});
		}
	}
	
}

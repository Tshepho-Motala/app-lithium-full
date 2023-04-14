package lithium.service.geo.data.repositories;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class MaxMindGeoIp2CityRepository {

    private DatabaseReader cityDatabaseReader;
    private static volatile MaxMindGeoIp2CityRepository instance = null;

    private MaxMindGeoIp2CityRepository(File db) {
        // This creates the DatabaseReader object. To improve performance, reuse the object across lookups. The object is thread-safe.
        try {
            cityDatabaseReader = new DatabaseReader.Builder(db).build();
        } catch (IOException e) {
            log.error("There was an error reading the database. It could be caused by an unsupported Builder " +
                    "configuration: expected either File or URL, or an invalid attempt to open an unknown database type");
        }

    }

    public static MaxMindGeoIp2CityRepository getSingletonInstance(File db) {
        if (instance == null) {
            synchronized (MaxMindGeoIp2CityRepository.class) {
                if (instance == null) {
                    instance = new MaxMindGeoIp2CityRepository(db);
                }
            }
        }
        return instance;
    }

    public CityResponse getCityResponse(String address) {
        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            log.error("Could not determine InetAddress for: " + address + ", or a scope_id was specified for a global IPv6 address.");
            return null;
        }

        CityResponse cityResponse = null;

        try {
            cityResponse = cityDatabaseReader.city(ipAddress);
        } catch (AddressNotFoundException addressNotFoundException) {
            log.info("The address " + address + " is not in the City database.");
            return null;
        } catch (IOException | GeoIp2Exception ex) {
            log.error("An error occurred while returning the City response " + ex.getMessage());
        }

        return cityResponse;
    }

}

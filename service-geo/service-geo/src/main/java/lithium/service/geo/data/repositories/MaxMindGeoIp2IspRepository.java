package lithium.service.geo.data.repositories;

import com.maxmind.db.InvalidDatabaseException;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.IspResponse;
import lithium.service.geo.data.entities.Isp;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class MaxMindGeoIp2IspRepository {

    private DatabaseReader ispDatabaseReader;
    private static volatile MaxMindGeoIp2IspRepository instance = null;

    private MaxMindGeoIp2IspRepository(File db) {
        // This creates the DatabaseReader object. To improve performance, reuse the object across lookups. The object is thread-safe.
        try {
            ispDatabaseReader = new DatabaseReader.Builder(db).build();
        } catch (IOException e) {
            log.error("There was an error reading the database. It could be caused by an unsupported Builder " +
                    "configuration: expected either File or URL, or an invalid attempt to open an unknown database type");
        }

    }

    public static MaxMindGeoIp2IspRepository getSingletonInstance(File db) {
        if (instance == null) {
            synchronized (MaxMindGeoIp2IspRepository.class) {
                if (instance == null) {
                    instance = new MaxMindGeoIp2IspRepository(db);
                }
            }
        }
        return instance;
    }

    public Isp getIspByAddress(String address) {
        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            log.error("No IP address for the host could be found, or the a scope_id was specified for a global IPv6 " +
                    "address.");
        }

        IspResponse ispResponse = null;

        try {
            ispResponse = ispDatabaseReader.isp(ipAddress);
        } catch (AddressNotFoundException addressNotFoundException) {
            log.info("The address " + address + " is not in the ISP database.");
            return null;
        } catch (InvalidDatabaseException invalidDatabaseException) {
            log.error("Unexpected data type returned. The GeoIP2 database may be corrupt.");
        } catch (IOException | GeoIp2Exception ex) {
            log.error("An error occurred while returning the ISP response " + ex.getMessage());
        }

        Isp isp = new Isp();

        isp.setAutonomousSystemNumber(ispResponse.getAutonomousSystemNumber());
        isp.setAutonomousSystemOrganization(ispResponse.getAutonomousSystemOrganization());
        isp.setIsp(ispResponse.getIsp());
        isp.setOrganization(ispResponse.getOrganization());

        return isp;
    }

}

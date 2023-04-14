package lithium.service.geo.data.repositories;

import com.maxmind.db.InvalidDatabaseException;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.AddressNotFoundException;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.ConnectionTypeResponse;
import lithium.service.geo.data.entities.ConnectionType;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class MaxMindGeoIP2ConnectionTypeRepository {

    private DatabaseReader connectionTypeDatabaseReader;
    private static volatile MaxMindGeoIP2ConnectionTypeRepository instance = null;

    private MaxMindGeoIP2ConnectionTypeRepository(File connectionTypeDbFile) {
        try {
            connectionTypeDatabaseReader = new DatabaseReader.Builder(connectionTypeDbFile).build();
        } catch (IOException e) {
            log.error("There was an error reading the database. It could be caused by an unsupported Builder " +
                    "configuration: expected either File or URL, or an invalid attempt to open an unknown database type");
        }
    }

    public static MaxMindGeoIP2ConnectionTypeRepository getSingletonInstance(File db) {
        if (instance == null) {
            synchronized (MaxMindGeoIP2ConnectionTypeRepository.class) {
                if (instance == null) {
                    instance = new MaxMindGeoIP2ConnectionTypeRepository(db);
                }
            }
        }
        return instance;
    }

    public ConnectionType findConnectionTypeByAddress(String address) {
        InetAddress ipAddress = null;
        try {
            ipAddress = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            log.error("No IP address for the host could be found, or the scope_id was specified for a global IPv6 " +
                    "address.");
        }

        ConnectionType connectionType = null;
        try {
            ConnectionTypeResponse connectionTypeResponse = connectionTypeDatabaseReader.connectionType(ipAddress);
            if ((connectionTypeResponse != null) && (connectionTypeResponse.getConnectionType() != null)) {
                connectionType = ConnectionType.builder().name(connectionTypeResponse.getConnectionType().toString())
                                               .build();
            }
        } catch (AddressNotFoundException addressNotFoundException) {
            log.info("The address " + address + " is not in the Connection Type database.");
            return null;
        } catch (InvalidDatabaseException invalidDatabaseException) {
            log.error("Unexpected data type returned. The GeoIP2 database may be corrupt.");
        } catch (IOException | GeoIp2Exception ex) {
            log.error("An error occurred while returning the Connection Type response " + ex.getMessage());
        }
        return connectionType;
    }
}

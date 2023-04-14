package lithium.service.geo.neustar.services;

import lithium.service.geo.data.entities.ConnectionType;
import lithium.service.geo.neustar.exception.IPV4AddressNotFoundException;
import lithium.service.geo.data.entities.Isp;
import lithium.service.geo.neustar.exception.InvalidIPV4AddressException;
import lithium.service.geo.neustar.objects.ConnectionTypeDataType;
import lithium.service.geo.neustar.objects.CountryDataType;
import lithium.service.geo.neustar.objects.IpInfo;
import lithium.service.geo.neustar.objects.IspDataType;
import lithium.service.geo.neustar.objects.LocationType;
import lithium.service.geo.neustar.objects.NetworkDataType;
import lithium.service.geo.objects.Location;
import lithium.service.geo.services.MaxMindLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NeustarCompatibleMaxMindLookupService {

    /**
     * OWASP Validation IP Regex
     */
    private static final String IPV4_REGEX = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
            "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    private static final Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    @Autowired
    private MaxMindLookupService maxMindLookupService;

    public IpInfo lookup(String ipv4) {
        NetworkDataType networkData = null;

        if (!isValidIPV4Address(ipv4)) {
            throw new InvalidIPV4AddressException(ipv4);
        }

        Location location = maxMindLookupService.lookup(ipv4);

        if (location == null) {
            throw new IPV4AddressNotFoundException(ipv4);
        }

        if (location.getNetwork() != null) {
            Isp isp = location.getNetwork().getIsp();
            ConnectionType connectionType = location.getNetwork().getConnectionType();

            if (isp != null) {

                String connectionTypeName = null;

                if (connectionType != null) {
                    connectionTypeName = connectionType.getName();
                }

                networkData = NetworkDataType.builder()
                                             .isp(IspDataType.builder()
                                                             .isp(isp.getIsp())
                                                             .autonomousSystemNumber(isp.getAutonomousSystemNumber())
                                                             .organization(isp.getOrganization())
                                                             .autonomousSystemOrganization(isp.getAutonomousSystemOrganization())
                                                             .build())
                                             .connectionTypeDataType(ConnectionTypeDataType.builder()
                                                                                           .name(connectionTypeName)
                                                                                           .build())
                                             .build();
            }
        }

        String country = null;
        String countryCode = null;

        if (location.getCountry() != null) {
            country = location.getCountry().getName();
            countryCode = location.getCountry().getCode();
        }

        CountryDataType countryDataType = CountryDataType.builder()
                                                         .country(country)
                                                         .countryCode(countryCode)
                                                         .build();

        LocationType locationType = LocationType.builder()
                                                .countryData(countryDataType)
                                                .build();

        return IpInfo.builder()
                     .location(locationType)
                     .networkData(networkData)
                     .build();
    }

    private boolean isValidIPV4Address(String ip) {

        if (ip == null) {
            return false;
        }

        Matcher matcher = IPV4_PATTERN.matcher(ip);

        return matcher.matches();
    }

}

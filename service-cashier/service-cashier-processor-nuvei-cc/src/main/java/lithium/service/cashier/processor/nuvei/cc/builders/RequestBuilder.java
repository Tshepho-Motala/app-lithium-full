package lithium.service.cashier.processor.nuvei.cc.builders;

import com.google.common.net.InetAddresses;
import com.safecharge.model.DeviceDetails;
import com.safecharge.model.MerchantInfo;
import com.safecharge.model.RestApiUserDetails;
import com.safecharge.model.UrlDetails;
import com.safecharge.model.UserAddress;
import com.safecharge.util.APIConstants;
import com.safecharge.util.Constants;
import lithium.config.LithiumConfigurationProperties;
import lithium.service.cashier.client.internal.DoProcessorRequest;
import lithium.service.cashier.processor.nuvei.exceptions.NuveiInvalidIPAddressException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Optional;

@Slf4j
public class RequestBuilder {
    @Autowired
    private LithiumConfigurationProperties lithiumProperties;
    @Value("${spring.application.name}")
    private String moduleName;
    @Value("${eureka.environment:livescore-develop}")
    protected String environment;

    protected UserAddress getBillingAddress(DoProcessorRequest request) {
        UserAddress billingAddress = new UserAddress();
        billingAddress.setEmail(request.getUser().getEmail());
        billingAddress.setCountry(request.getUser().getCountryCode());
        billingAddress.setFirstName(request.getUser().getFirstName());
        billingAddress.setLastName(request.getUser().getLastName());
        billingAddress.setPhone(request.getUser().getCellphoneNumber());

        if (request.getUser().getResidentialAddress() != null) {
            billingAddress.setAddress(request.getUser().getResidentialAddress().getAddressLine1());
            billingAddress.setCity(request.getUser().getResidentialAddress().getCity());
            billingAddress.setZip(request.getUser().getResidentialAddress().getPostalCode());
        }
        return billingAddress;
    }

    protected RestApiUserDetails getUserDetails(DoProcessorRequest request) {
        RestApiUserDetails userDetails = new RestApiUserDetails();
        userDetails.setEmail(request.getUser().getEmail());
        userDetails.setCountry(request.getUser().getCountryCode());
        userDetails.setFirstName(request.getUser().getFirstName());
        userDetails.setLastName(request.getUser().getLastName());
        userDetails.setPhone(request.getUser().getCellphoneNumber());
        userDetails.setDateOfBirth(request.getUser().getDateOfBirth().toString("YYYY-MM-dd"));

        if (request.getUser().getResidentialAddress() != null) {
            userDetails.setAddress(request.getUser().getResidentialAddress().getAddressLine1());
            userDetails.setCity(request.getUser().getResidentialAddress().getCity());
            userDetails.setZip(request.getUser().getResidentialAddress().getPostalCode());
        }
        return userDetails;
    }

    protected DeviceDetails getDeviceDetails(DoProcessorRequest request) throws NuveiInvalidIPAddressException {
        DeviceDetails deviceDetails = new DeviceDetails();
        deviceDetails.setIpAddress(getIPAddress(request));
        deviceDetails.setDeviceOS(request.getUser().getOs());
        deviceDetails.setBrowser(request.getUser().getBrowser());
        return deviceDetails;
    }

    private String getIPAddress(DoProcessorRequest request) throws NuveiInvalidIPAddressException {
        InetAddress inetAddress = InetAddresses.forString(Optional.ofNullable(request.stageOutputData(1).get("ipAddress")).orElse(request.getUser().getLastKnownIP()));

        //Nuvei does not support ipv6
        if (inetAddress instanceof Inet6Address) {
            log.error("Nuvei does not support IPv6 address" + inetAddress.getHostAddress() + " TransactionId: " + request.getTransactionId());
            throw new NuveiInvalidIPAddressException(inetAddress.getHostAddress());
        }

        return inetAddress.getHostAddress();
    }

    public MerchantInfo getMerchantInfo(DoProcessorRequest request) throws Exception {
        return new MerchantInfo(request.getProperty("merchant_key"),
                                request.getProperty("merchant_id"),
                                request.getProperty("merchant_site_id"),
                                Boolean.parseBoolean(request.getProperty("test")) ? APIConstants.Environment.INTEGRATION_HOST.getUrl() : APIConstants.Environment.PRODUCTION_HOST.getUrl(),
                                Constants.HashAlgorithm.SHA256);
    }

    protected UrlDetails getUrlDetails(DoProcessorRequest request) {
        UrlDetails urlDetails = new UrlDetails();
        urlDetails.setNotificationUrl(gatewayPublicUrl() + "/public/webhook/");
        return urlDetails;
    }

    public String gatewayPublicUrl() {
        return  lithiumProperties.getGatewayPublicUrl()  + "/" + moduleName;
    }
}

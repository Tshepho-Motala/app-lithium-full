package lithium.service.cashier.processor.bluem.ideal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lithium.service.cashier.processor.bluem.api.data.EPaymentInterfaceType;
import lithium.service.cashier.processor.bluem.api.data.EPaymentStatusRequestType;
import lithium.service.cashier.processor.bluem.api.data.EPaymentStatusUpdateType;
import lithium.service.cashier.processor.bluem.api.data.EPaymentTransactionRequestType;
import lithium.service.cashier.processor.bluem.api.data.EPaymentTransactionResponseType;
import lithium.service.cashier.processor.bluem.api.data.ModeSimpleType;
import lithium.service.cashier.processor.bluem.exceptions.BluemConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

@Slf4j
public class BluemApiService {
    private RestTemplate restTemplate;
    private ObjectMapper mapper;

    private static final String FILES_COUNT = "1";
    private static final String TRANSACTION_REQUEST_TYPE = "TransactionRequest";
    private static final String STATUS_REQUEST_TYPE = "StatusRequest";

    private String apiUrl;
    private String senderId;
    private String brandId;
    private String token;

    public static BluemApiService instance(String apiUrl, String senderId, String brandId, String token, RestTemplate restTemplate, ObjectMapper mapper) {
        return new BluemApiService(apiUrl, senderId, brandId, token, restTemplate, mapper);
    }

    public BluemApiService(String apiUrl, String senderId, String brandId, String token, RestTemplate restTemplate, ObjectMapper mapper) {
        this.apiUrl = apiUrl;
        this.senderId = senderId;
        this.brandId = brandId;
        this.token = token;
        this.mapper = mapper;
        this.restTemplate = restTemplate;
    }

    public EPaymentTransactionResponseType sendPaymentRequest(EPaymentTransactionRequestType request, Date now) throws Exception {
        try {
            EPaymentInterfaceType paymentInterface = getPaymentInterface(TRANSACTION_REQUEST_TYPE, now);
            paymentInterface.setPaymentTransactionRequest(request);
            String requestBody = mapper.writeValueAsString(paymentInterface);
            HttpEntity<?> entity = new HttpEntity<>(requestBody, getHeaders("PTX", now));

            log.info("Bluem payment request: ", requestBody);
            ResponseEntity<String> exchange = restTemplate.exchange(apiUrl + "/createTransactionWithToken?token=" + token, HttpMethod.POST, entity, String.class, new HashMap<String,String>());

            if (!exchange.getStatusCode().is2xxSuccessful()) {
                log.error("Bluem responded with http error code: " + exchange.getStatusCodeValue() + ". Reason:" + exchange.getBody());
                //TODO: handle exceptions
                throw new BluemConnectionException(exchange.getStatusCodeValue(), exchange.getBody());
            }
            log.info("Bluem payment response: ", exchange.getBody());

            EPaymentInterfaceType response = mapper.readValue(exchange.getBody(), EPaymentInterfaceType.class);
            return response.getPaymentTransactionResponse();

        } catch (BluemConnectionException ce) {
            throw ce;
        } catch (Exception e) {
            log.error("Failed to send Bluem payment request" + request.toString() + "Exception: " + e.getMessage(), e);
            throw new BluemConnectionException(500, "Failed to send Bluem payment request. Exception: " + e.getMessage());
        }
    }

    public EPaymentStatusUpdateType getPaymentStatus(String transactionId, String entranceCode) throws Exception {

        EPaymentStatusRequestType statusRequest = new EPaymentStatusRequestType();
        statusRequest.setEntranceCode(entranceCode);
        statusRequest.setTransactionID(transactionId);

        return getPaymentStatus(statusRequest);
    }

    public EPaymentStatusUpdateType getPaymentStatus(EPaymentStatusRequestType statusRequest) throws Exception {
        try {
            Date now = new Date();

            EPaymentInterfaceType paymentInterface = getPaymentInterface(STATUS_REQUEST_TYPE, now);
            paymentInterface.setPaymentStatusRequest(statusRequest);

            String requestBody = mapper.writeValueAsString(paymentInterface);
            HttpEntity<?> entity = new HttpEntity<>(requestBody, getHeaders("PSX", now));

            log.info("Bluem payment request: ", requestBody);
            ResponseEntity<String> exchange = restTemplate.exchange(apiUrl + "/requestTransactionStatusWithToken?token=" + token, HttpMethod.POST, entity, String.class, new HashMap<String,String>());

            if (!exchange.getStatusCode().is2xxSuccessful()) {
                log.error("Bluem responded with http error code: " + exchange.getStatusCodeValue() + ". Reason:" + exchange.getBody());
                //TODO: handle exceptions
                throw new BluemConnectionException(exchange.getStatusCodeValue(), exchange.getBody());
            }
            log.info("Bluem payment response: ", exchange.getBody());

            EPaymentInterfaceType response = mapper.readValue(exchange.getBody(), EPaymentInterfaceType.class);
            return response.getPaymentStatusUpdate();

        } catch (BluemConnectionException ce) {
            throw ce;
        } catch (Exception e) {
            log.error("Failed to send Bluem payment status for transactionId" + statusRequest.getTransactionID() + "Exception: " + e.getMessage(), e);
            throw new BluemConnectionException(500, "Failed to send Bluem payment status request. Exception: " + e.getMessage());
        }
    }


    private EPaymentInterfaceType getPaymentInterface(String requestType, Date now) {
        EPaymentInterfaceType finalrequest = new EPaymentInterfaceType();
        finalrequest.setMode(ModeSimpleType.DIRECT);
        finalrequest.setType(TRANSACTION_REQUEST_TYPE);
        finalrequest.setSenderID(senderId);
        finalrequest.setVersion("1.0");
        finalrequest.setCreateDateTime(getTimeString(now));
        finalrequest.setMessageCount(1);
        return finalrequest;
    }

    private MultiValueMap<String, String> getHeaders(String requestType, Date now) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
        headers.add("x-ttrs-date", getRFC1123Time(now));
        headers.add("x-ttrs-files-count", FILES_COUNT);
        headers.add("x-ttrs-filename", requestType + "-" + senderId + "-BSP1-" + getTimeForFileName(now) + ".xml");
        headers.add("Content-Type",  "application/xml;type=" + requestType + ";charset=utf-8;");
        return headers;
    }

    private static String getRFC1123Time(Date now) {
        if (now == null) {
            now = new Date();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(now);
    }

    public static String getTimeForFileName(Date now) {
        if (now == null) {
            now = new Date();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(now);
    }

    public static String getTimeString(Date now) {
        if (now == null) {
            now = new Date();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(now);
    }
}

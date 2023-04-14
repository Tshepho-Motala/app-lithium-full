package lithium.service.sms.provider.mobivate.controller;

import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.sms.client.internal.DoCallbackClient;
import lithium.service.sms.client.internal.DoProviderResponse;
import lithium.service.sms.client.internal.DoProviderResponseStatus;
import lithium.service.sms.provider.mobivate.data.MobivateWebhook;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@Slf4j
public class WebHookController {
    private static final String DELIVERED = "DELIVERED";
    @Autowired
    LithiumServiceClientFactory serviceFactory;

    @PostMapping(value = "/callback/mobivate", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
    public @ResponseBody
    void callback(@RequestParam String xml) {
        String decodedString = decodeString(xml);
        log.info("Received callback from mobivate provider: " + decodedString);
        StringReader reader = new StringReader(decodedString);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(MobivateWebhook.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            MobivateWebhook webhook = (MobivateWebhook) jaxbUnmarshaller.unmarshal(reader);
            if (webhook != null && webhook.getStatus().equalsIgnoreCase(DELIVERED)) {
                DoProviderResponse doProviderResponse = DoProviderResponse.builder()
                        .smsId(Long.parseLong(webhook.getClientReference()))
                        .status(DoProviderResponseStatus.SUCCESS)
                        .providerReference(webhook.getDeliveryMessageId())
                        .message("Successfully delivered")
                        .build();

                log.debug("Try to set DoProviderResponseStatus.SUCCESS to SmsId " + doProviderResponse.getSmsId());
                DoCallbackClient client = serviceFactory.target(DoCallbackClient.class, "service-sms", true);
                client.doProviderCallback(doProviderResponse);
            }
        } catch (JAXBException e) {
            log.warn("Service can't unmarshall callback: " + decodedString + " in MobivateWebhook format" + e.getMessage());
        } catch (LithiumServiceClientFactoryException ex) {
            log.error("There was a problem processing the callback from the mobivate sms provider " + decodedString, ex);
        }
    }


    private String decodeString(String url) {
        try {
            return URLDecoder.decode(url, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("can't decode url - " + url + " cause by - " + e.getMessage());
            return url;
        }
    }
}

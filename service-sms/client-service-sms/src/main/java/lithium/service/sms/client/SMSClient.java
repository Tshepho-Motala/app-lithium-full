package lithium.service.sms.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="service-sms")
public interface SMSClient {
}
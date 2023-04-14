package lithium.service.document.provider.api.controller;

import lithium.service.document.provider.api.schema.JobData;
import lithium.service.document.provider.service.HelloSodaProfileFKService;
import lithium.service.document.provider.service.LithiumDocumentService;
import lithium.service.user.client.exceptions.UserClientServiceFactoryException;
import lithium.service.user.client.exceptions.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/webhook")
@AllArgsConstructor
@Slf4j
public class WebHookController {

    private final LithiumDocumentService lithiumDocumentService;
    private final HelloSodaProfileFKService fkService;

    @PostMapping(value = "/notify", consumes = {"application/json"})
    public ResponseEntity notifyUrl(@RequestBody JobData jobData) throws Exception, UserClientServiceFactoryException, UserNotFoundException {
        log.debug("Receive notify from hello soda : " + jobData);
        return lithiumDocumentService.updateDocument(jobData.getJobId());
    }

    @PostMapping(value = "/facebook/notify", consumes = {"application/json"})
    public ResponseEntity fkWebhookSubmit(@RequestBody JobData jobData) throws Exception, UserClientServiceFactoryException, UserNotFoundException {
        log.debug("Receive facebook notify from hello soda : " + jobData);
        return fkService.processFacebookHSReport(jobData.getJobId());
    }
}

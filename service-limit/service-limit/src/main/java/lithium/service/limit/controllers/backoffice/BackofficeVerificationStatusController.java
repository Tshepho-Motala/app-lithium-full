package lithium.service.limit.controllers.backoffice;

import lithium.service.Response;
import lithium.service.limit.data.entities.VerificationStatus;
import lithium.service.limit.data.repositories.VerificationStatusRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/backoffice/verification/status")
public class BackofficeVerificationStatusController {
    private final VerificationStatusRepository verificationStatusRepository;

    @GetMapping("/all")
    public Response<Iterable<VerificationStatus>> all() {
        return Response.<Iterable<VerificationStatus>>builder().data(verificationStatusRepository.findAll()).build();
    }
}

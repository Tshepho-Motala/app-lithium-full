package lithium.service.user.controllers.backoffice;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.user.client.enums.BiometricsStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/backoffice/biometrics-statuses")
public class BiometricsStatusController {

  @GetMapping
  public Response<Iterable<BiometricsStatus>> getAvailableBiometricsStatuses() {
    return Response.<Iterable<BiometricsStatus>>builder().data(List.of(BiometricsStatus.values())).status(Status.OK).build();
  }

}

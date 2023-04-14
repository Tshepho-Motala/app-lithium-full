package lithium.service.games.controllers.backoffice;

import lithium.service.Response;
import lithium.service.games.client.objects.ProgressiveJackpotFeedRegistration;
import lithium.service.games.data.entities.progressivejackpotfeeds.ProgressiveJackpotFeed;
import lithium.service.games.services.ProgressiveJackpotFeedService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/jackpot-feed/progressive")
public class ProgressiveJackpotFeedController {

    @Autowired
    ProgressiveJackpotFeedService progressiveJackpotFeedService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/registered-feed/{id}/get")
    public Response<ProgressiveJackpotFeedRegistration> findById(
            @PathVariable("id") Long id
    ) {
        ProgressiveJackpotFeed progressiveJackpotFeedRegistration = progressiveJackpotFeedService.getRegisteredFeedById(id);
        ProgressiveJackpotFeedRegistration progressiveJackpotFeedRegistrationById = modelMapper.map(progressiveJackpotFeedRegistration, new TypeToken<ProgressiveJackpotFeedRegistration>(){}.getType());
        return Response.<ProgressiveJackpotFeedRegistration>builder().data(progressiveJackpotFeedRegistrationById).status(Response.Status.OK).build();
    }

    @PostMapping("/registered-feed/{id}/toggle-enabled")
    public Response<ProgressiveJackpotFeedRegistration> toggleEnabled(
            @PathVariable("id") Long id
    ) {
        ProgressiveJackpotFeed progressiveJackpotFeedRegistration = progressiveJackpotFeedService.toggledEnabledById(id);
        ProgressiveJackpotFeedRegistration progressiveJackpotFeedRegistrationById = modelMapper.map(progressiveJackpotFeedRegistration, new TypeToken<ProgressiveJackpotFeedRegistration>(){}.getType());
        return Response.<ProgressiveJackpotFeedRegistration>builder().data(progressiveJackpotFeedRegistrationById).status(Response.Status.OK).build();
    }
}

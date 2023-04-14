package lithium.service.games.controllers.backoffice;

import lithium.service.Response;
import lithium.service.games.data.entities.Channel;
import lithium.service.games.services.GameChannelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/backoffice/get-channels")
public class BackOfficeGameChannelsController {

    @Autowired
    GameChannelService gameChannelService;

    @PostMapping("/find-all")
    public Response<List<Channel>> findAllChannels(
    ) {
        return Response.<List<Channel>>builder().data(gameChannelService.getAllChannels()).status(Response.Status.OK).build();
    }
}

package lithium.service.xp.controllers;

import lithium.service.Response;
import lithium.service.xp.data.entities.Level;
import lithium.service.xp.services.XPService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@Slf4j
@RestController
@RequestMapping("/xp")
public class XPController {

    @Autowired
    private XPService  xpService;

    @GetMapping("/level")
    public Response<Level> getLevel(@RequestParam("userGuid") String userGuid,@RequestParam("domainName") String domainName){
        try{
            Level level=xpService.getLevelByPlayerGuid(userGuid,domainName);
            return Response.<Level>builder().data(level).status(OK).build();
        }catch (Exception ex){
            log.error("Error getting level for user: {}, domain: {}, {}", userGuid,domainName,ex);
            return Response.<Level>builder().data(null).status(INTERNAL_SERVER_ERROR).build();
        }
    }
}

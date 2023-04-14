package lithium.service.games.controllers.backoffice;

import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.games.data.entities.GameStudio;
import lithium.service.games.services.GameStudioService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/backoffice/{domainName}/game-studio")
public class GameStudioController {
    @Autowired
    GameStudioService gameStudioService;

    @PostMapping("/{id}")
    public Response<GameStudio> findGameStudio(@PathVariable("domainName") String domainName,
                                    @PathVariable("id") GameStudio gameStudio, LithiumTokenUtil tokenUtil) {
        try {
            DomainValidationUtil.validate(domainName, gameStudio.getDomain().getName());
            if (gameStudio.getDeleted().booleanValue())
                throw new Status500InternalServerErrorException("Game studio has been deleted");
            return Response.<GameStudio>builder().data(gameStudio).status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to retrieve GameStudio [domainName="+domainName+", gameStudio="+gameStudio
                    +", tokenUtil.guid="+tokenUtil.guid()+"] " + e.getMessage(), e);
            return Response.<GameStudio>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PutMapping("/{id}")
    public Response<GameStudio> update(@PathVariable("domainName") String domainName,
                                     @PathVariable("id") GameStudio gameStudio, @RequestBody GameStudio update,
                                     LithiumTokenUtil tokenUtil) {
        try {
            DomainValidationUtil.validate(domainName, gameStudio.getDomain().getName());
            gameStudioService.updateGameStudio(domainName, gameStudio, update, tokenUtil.guid());
            return Response.<GameStudio>builder().data(gameStudio).status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to update GameStudio [domainName="+domainName+", gameStudio="+gameStudio
                    +", update="+update+", tokenUtil.guid="+tokenUtil.guid()+"] " + e.getMessage(), e);
            return Response.<GameStudio>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable("domainName") String domainName,
                                 @PathVariable("id") GameStudio gameStudio,
                                 LithiumTokenUtil tokenUtil) {
        try {
            DomainValidationUtil.validate(domainName, gameStudio.getDomain().getName());
            if(gameStudio != null && gameStudio.getDeleted()) {
                log.error("Failed to delete GameStudio [domainName="+domainName+", gameStudio="+gameStudio
                        +", tokenUtil.guid="+tokenUtil.guid()+"] Already in a deleted state");
                return Response.<Void>builder().status(Response.Status.NOT_FOUND).build();
            }
            gameStudioService.deleteGameStudio(domainName, gameStudio,  tokenUtil.guid());
            return Response.<Void>builder().status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to delete GameStudio [domainName="+domainName+", gameStudio="+gameStudio
                    +", tokenUtil.guid="+tokenUtil.guid()+"] " + e.getMessage(), e);
            return Response.<Void>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/find-by-domain")
    public Response<List<GameStudio>> findByDomain(@PathVariable("domainName") String domainName) {
        return Response.<List<GameStudio>>builder().data(gameStudioService.findByDomain(domainName)).status(Response.Status.OK)
                .build();
    }

    @PostMapping("/table")
    public DataTableResponse<GameStudio> table(@PathVariable("domainName") String domainName,
                                               @RequestParam(name = "deleted", required = false, defaultValue = "false") Boolean deleted,
                                               DataTableRequest request) {
        Page<GameStudio> result = gameStudioService.findByDomain(domainName, deleted, request.getSearchValue(),
                request.getPageRequest());
        return new DataTableResponse<>(request, result);
    }

    @PostMapping("/add")
    public Response<GameStudio> add(@PathVariable("domainName") String domainName,
                                  @RequestBody GameStudio gameStudio, LithiumTokenUtil tokenUtil) {
        try {
            gameStudio = gameStudioService.addGameStudio(domainName, gameStudio, tokenUtil.guid());
            return Response.<GameStudio>builder().data(gameStudio).status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to add GameStudio [domainName="+domainName+", gameStudio="+gameStudio
                    +", tokenUtil.guid="+tokenUtil.guid()+"] " + e.getMessage(), e);
            return Response.<GameStudio>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PostMapping("/{id}/changelogs")
    public Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p) throws Exception {
        return gameStudioService.changeLogs(id, p);
    }
}

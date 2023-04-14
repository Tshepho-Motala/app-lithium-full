package lithium.service.games.controllers.backoffice;


import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.games.data.entities.Game;
import lithium.service.games.data.entities.GameType;
import lithium.service.games.data.entities.GameTypeEnum;
import lithium.service.games.services.GameService;
import lithium.service.games.services.GameTypeService;
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
@RequestMapping("/backoffice/{domainName}/game-types")
public class GameTypeController {
    @Autowired
    private GameTypeService service;

    @Autowired
    private GameService gameService;

    @GetMapping("/{id}")
    public Response<GameType> get(@PathVariable("domainName") String domainName,
                                  @PathVariable("id") GameType gameType, LithiumTokenUtil tokenUtil) {
        try {
            DomainValidationUtil.validate(domainName, gameType.getDomain().getName());
            if (gameType.getDeleted().booleanValue())
                throw new Status500InternalServerErrorException("Game type has been deleted");
            return Response.<GameType>builder().data(gameType).status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to retrieve GameType [domainName="+domainName+", gameType="+gameType
                    +", tokenUtil.guid="+tokenUtil.guid()+"] " + e.getMessage(), e);
            return Response.<GameType>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @PutMapping("/{id}")
    public Response<GameType> update(@PathVariable("domainName") String domainName,
                                         @PathVariable("id") GameType gameType, @RequestBody GameType update,
                                         LithiumTokenUtil tokenUtil) {
        try {
            DomainValidationUtil.validate(domainName, gameType.getDomain().getName());
            service.updateGameType(domainName, gameType, update, tokenUtil.guid());
            return Response.<GameType>builder().data(gameType).status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to update GameType [domainName="+domainName+", gameType="+gameType
                    +", update="+update+", tokenUtil.guid="+tokenUtil.guid()+"] " + e.getMessage(), e);
            return Response.<GameType>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @DeleteMapping("/{id}")
    public Response<Void> delete(@PathVariable("domainName") String domainName,
                                   @PathVariable("id") GameType gameType,
                                   LithiumTokenUtil tokenUtil) {
        try {
            DomainValidationUtil.validate(domainName, gameType.getDomain().getName());
            if(gameType != null && gameType.getDeleted()) {
                log.error("Failed to delete GameType [domainName="+domainName+", gameType="+gameType
                        +", tokenUtil.guid="+tokenUtil.guid()+"] Already in a deleted state");
                return Response.<Void>builder().status(Response.Status.NOT_FOUND).build();
            }
            service.deleteGameType(domainName, gameType,  tokenUtil.guid());
            return Response.<Void>builder().status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to delete GameType [domainName="+domainName+", gameType="+gameType
                    +", tokenUtil.guid="+tokenUtil.guid()+"] " + e.getMessage(), e);
            return Response.<Void>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @GetMapping("/find-by-domain")
    public Response<List<GameType>> findByDomain(@PathVariable("domainName") String domainName) {
        return Response.<List<GameType>>builder().data(service.findByDomain(domainName)).status(Response.Status.OK)
                .build();
    }

    @GetMapping("/find-by-domain-and-type")
    public Response<List<GameType>> findByDomain(@PathVariable("domainName") String domainName,
                                                 @RequestParam("type") String type) {
        GameTypeEnum enumType = GameTypeEnum.fromType(type);
        return Response.<List<GameType>>builder().data(service.findByDomainAndType(domainName, enumType)).status(Response.Status.OK)
                .build();
    }

    @GetMapping("/table")
    public DataTableResponse<GameType> table(@PathVariable("domainName") String domainName,
                                                 @RequestParam(name = "deleted", required = false, defaultValue = "false") Boolean deleted,
                                                 DataTableRequest request) {
        Page<GameType> result = service.findByDomain(domainName, deleted, request.getSearchValue(),
                request.getPageRequest());
        return new DataTableResponse<>(request, result);
    }

    @PostMapping("/add")
    public Response<GameType> add(@PathVariable("domainName") String domainName,
                                      @RequestBody GameType gameType, LithiumTokenUtil tokenUtil) {
        try {
            gameType = service.add(domainName, gameType, tokenUtil.guid());
            return Response.<GameType>builder().data(gameType).status(Response.Status.OK).build();
        } catch (Exception e) {
            log.error("Failed to add GameType [domainName="+domainName+", gameType="+gameType
                    +", tokenUtil.guid="+tokenUtil.guid()+"] " + e.getMessage(), e);
            return Response.<GameType>builder().status(Response.Status.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage()).build();
        }
    }

    @GetMapping("/{id}/changelogs")
    private @ResponseBody
    Response<ChangeLogs> changeLogs(@PathVariable Long id, @RequestParam int p) throws Exception {
        return service.changeLogs(id, p);
    }
}

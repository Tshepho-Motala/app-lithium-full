package lithium.service.user.mass.action.api.backoffice.controller;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.mass.action.api.backoffice.schema.FileDataSummaryResponse;
import lithium.service.user.mass.action.api.backoffice.schema.ActionsRequest;
import lithium.service.user.mass.action.api.backoffice.schema.ProgressResponse;
import lithium.service.user.mass.action.data.entities.FileData;
import lithium.service.user.mass.action.data.entities.FileUpload;
import lithium.service.user.mass.action.data.entities.UploadType;
import lithium.service.user.mass.action.exceptions.Status404DataRecordNotFoundException;
import lithium.service.user.mass.action.exceptions.Status404MassActionNotFoundException;
import lithium.service.user.mass.action.services.FileUploadService;
import lithium.service.user.mass.action.data.entities.UploadStatus;
import lithium.service.user.mass.action.services.MassActionService;
import java.util.Objects;
import lithium.tokens.LithiumTokenUtil;
import lithium.tokens.LithiumTokenUtilService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/backoffice/{domainName}/{uploadType}")
public class UserMassActionController {

    @Autowired LithiumTokenUtilService tokenService;
    @Autowired FileUploadService fileUploadService;
    @Autowired MassActionService massUpdateService;

    @PostMapping("/upload/csv")
    public Response<FileUpload> uploadCSV(@PathVariable("domainName") String domainName,
                                          @PathVariable("uploadType") UploadType uploadType,
                                          @RequestPart("file") final MultipartFile file,
                                          @RequestPart(name = "bonusCode", required = false) String bonusCode,
                                          @RequestPart(name = "defaultBonusAmount", required = false) String defaultBonusAmount,
                                          @RequestPart(name = "allowDuplicates", required = false) String allowDuplicates,
                                          @RequestPart(name = "bonusDescription", required = false) String bonusDescription,
                                          LithiumTokenUtil util,
                                          Principal principal) {
        log.debug("CSV file received(" + file.getOriginalFilename() +
                "): Creating a new user mass action for " + domainName);

        switch (uploadType) {
            case PLAYER_INFO: {
                return Response.<FileUpload>builder()
                        .data(fileUploadService.uploadPlayerCSV(domainName, file, ActionsRequest.builder().authorGuid(util.guid()).build()))
                        .status(Response.Status.OK)
                        .build();
            }
            default: { //BONUS_CASH && BONUS_FREESPIN && BONUS_INSTANT && BONUS_CASINOCHIP
                return Response.<FileUpload>builder()
                        .data(fileUploadService.uploadBonusCSV(domainName, file, principal,
                                bonusCode, Double.valueOf(defaultBonusAmount), Boolean.valueOf(allowDuplicates), bonusDescription, uploadType))
                        .status(Response.Status.OK)
                        .build();
            }
        }
    }

    @GetMapping("/progress/{stageName}")
    public Response<ProgressResponse> getProgress(@PathVariable("domainName") String domainName,
                                                  @PathVariable("uploadType") UploadType uploadType,
                                                  @PathVariable("stageName") String stageName,
                                                  @RequestParam("id") Long id) {
        return Response.<ProgressResponse>builder()
                .data(fileUploadService.getFileUploadProgress(domainName, stageName, id))
                .status(Response.Status.OK)
                .build();
    }

    @GetMapping("/in-progress")
    public Response<FileUpload> getUploadStatus(@PathVariable("domainName") String domainName,
                                                @PathVariable("uploadType") UploadType uploadType,
                                                @RequestParam("id") Long id) {
        return Response.<FileUpload>builder()
                .status(Response.Status.OK)
                .data(fileUploadService.getFileUploadStatus(id))
                .build();
    }

    @GetMapping("/table")
    public DataTableResponse<FileData> getTable(@PathVariable("domainName") String domainName,
                                                @PathVariable("uploadType") UploadType uploadType,
                                                @RequestParam("order[0][column]") String orderColumn,
                                                @RequestParam("order[0][dir]") String orderDirection,
                                                @RequestParam("id") Long id,
                                                DataTableRequest request) {
        if (request == null) {
            PageRequest pageRequest = PageRequest.of(0, 25, Sort.Direction.ASC, "id");
            request = new DataTableRequest();
            request.setPageRequest(pageRequest);
        }

        Sort sort = Sort.by(Sort.Direction.fromString(orderDirection), "rowNumber");
        if (orderColumn.equals("0"))
            sort = Sort.by(Sort.Direction.fromString(orderDirection), "rowNumber");
        else if (orderColumn.equals("1"))
            sort = Sort.by(Sort.Direction.fromString(orderDirection), "playerId");
        else if (orderColumn.equals("3"))
            sort = Sort.by(Sort.Direction.fromString(orderDirection), "amount");

        request.setPageRequest(PageRequest.of(request.getPageRequest().getPageNumber(),
                Math.min(request.getPageRequest().getPageSize(), 100),
                sort));

        Page<FileData> pbhPage = fileUploadService.getFileData(id, request);

        return new DataTableResponse<>(request, pbhPage);
    }

    @PostMapping("/process")
    public Response<FileUpload> processFileUpload(@PathVariable("domainName") String domainName,
                                                        @PathVariable("uploadType") UploadType uploadType,
                                                        @RequestBody ActionsRequest request) throws Status404MassActionNotFoundException {
        log.debug("Actions Request -> {}", request);
        switch (uploadType) {
            case PLAYER_INFO: {
                return Response.<FileUpload>builder()
                        .data(fileUploadService.processPlayerFileUpload(request))
                        .status(Response.Status.OK)
                        .build();
            }
            default: { //BONUS_CASH && BONUS_FREESPIN
                return Response.<FileUpload>builder()
                        .data(fileUploadService.processBonusFileUpload(request))
                        .status(Response.Status.OK)
                        .build();
            }
        }
    }

    @GetMapping("/summary")
    public Response<FileDataSummaryResponse> getFileDataSummary(@PathVariable("domainName") String domainName,
                                                                @PathVariable("uploadType") UploadType uploadType,
                                                                @RequestParam("id") Long id) {
        switch (uploadType) {
            case PLAYER_INFO: {
                return Response.<FileDataSummaryResponse>builder()
                        .data(fileUploadService.getPlayerFileUploadSummary(id))
                        .status(Response.Status.OK)
                        .build();
            }
            default: { //BONUS_CASH && BONUS_FREESPIN
                return Response.<FileDataSummaryResponse>builder()
                        .data(fileUploadService.getBonusFileUploadSummary(id))
                        .status(Response.Status.OK)
                        .build();
            }
        }
    }

    @DeleteMapping("/remove/table/{rowNumber}")
    public void removeFileDataRecord(@PathVariable("domainName") String domainName,
                                     @PathVariable("uploadType") UploadType uploadType,
                                     @PathVariable("rowNumber") Long rowNumber,
                                     @RequestParam("id") Long id) throws Status404DataRecordNotFoundException, Status404MassActionNotFoundException {
        fileUploadService.removeFileDataRecord(id, rowNumber);
    }

    @GetMapping("/history/table")
    public DataTableResponse<FileUpload> getHistoryTable(@PathVariable("domainName") String domainName,
                                                         @PathVariable("uploadType") UploadType uploadType,
                                                         @RequestParam("order[0][column]") String orderColumn,
                                                         @RequestParam("order[0][dir]") String orderDirection,
                                                         Principal principal,
                                                         DataTableRequest request) {
        if (request == null) {
            PageRequest pageRequest = PageRequest.of(0, 25, Sort.Direction.ASC, "id");
            request = new DataTableRequest();
            request.setPageRequest(pageRequest);
        }

        Sort sort = request.getPageRequest().getSort();
//        if (orderColumn.equals("0"))
//            sort = new Sort(Sort.Direction.fromString(orderDirection), "createdDate");
//        if (orderColumn.equals("1"))
//            sort = new Sort(Sort.Direction.fromString(orderDirection), "playerId");

        request.setPageRequest(PageRequest.of(request.getPageRequest().getPageNumber(),
                Math.min(request.getPageRequest().getPageSize(), 100),
                sort));

        Page<FileUpload> pbhPage = fileUploadService.findFileUploads(domainName, principal, uploadType, request);

        return new DataTableResponse<>(request, pbhPage);
    }
}

package lithium.service.document.generation.controllers;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.document.generation.client.enums.DocumentGenerationStatus;
import lithium.service.document.generation.client.objects.GenerateCsvRequest;
import lithium.service.document.generation.data.objects.CsvGenerationStatus;
import lithium.service.document.generation.data.objects.DocumentGenerateBO;
import lithium.service.document.generation.services.CsvExportService;
import lithium.service.document.generation.services.GenerationProcessingControlService;
import lithium.service.document.generation.services.GenerationsService;
import lithium.tokens.LithiumTokenUtil;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@AllArgsConstructor
public class DocumentGenerationControllerBO {

    private GenerationsService generationsService;
    private CsvExportService csvExportService;
    private GenerationProcessingControlService service;

    @PostMapping(value = "/document/generate")
    public Response<CsvGenerationStatus> generate(@RequestBody GenerateCsvRequest csvRequest, LithiumTokenUtil tokenUtil) {

        return Response.<CsvGenerationStatus>builder()
                .data(csvExportService.generate(csvRequest, tokenUtil.guid()))
                .build();
    }

    @GetMapping(value = "/document/{id}/status")
    public Response<CsvGenerationStatus> status(@PathVariable("id") Long id){

        return Response.<CsvGenerationStatus>builder()
                .data(service.getGenerationStatus(id))
                .build();
    }

    @PostMapping(value = "/document/{id}/cancel")
    public void cancelDocumentGeneration(@PathVariable("id") Long id) {
        service.changeGenerationStatus(id, DocumentGenerationStatus.CANCELED);
    }

    @GetMapping(value = "/document/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable("id") Long id) throws IOException {
        return service.download(id);
    }

    @GetMapping(value = "/document/list")
    public DataTableResponse<DocumentGenerateBO> list(@RequestParam("pageSize") int pageSize,
                                                      @RequestParam("page") int page,
                                                      LithiumTokenUtil tokenUtil) {
        DataTableRequest request = new DataTableRequest();
        request.setPageRequest(PageRequest.of(page, pageSize));

        return generationsService.findUserGenerations(tokenUtil.guid(), request);
    }
}

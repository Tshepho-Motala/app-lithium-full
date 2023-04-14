package lithium.service.user.provider.threshold.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Date;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.user.provider.threshold.data.entities.PlayerThresholdHistory;
import lithium.service.user.provider.threshold.services.ThresholdHistoryReportService;
import lithium.service.user.threshold.client.UserProviderThresholdReportClient;
import lithium.service.user.threshold.client.dto.ThresholdsFilterRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/threshold-history")
public class ThresholdHistoryReportController implements UserProviderThresholdReportClient {

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private ThresholdHistoryReportService thresholdHistoryReportService;

    @GetMapping("/table")
    public DataTableResponse<PlayerThresholdHistory> table(
            @RequestParam(name = "startDateTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date startDateTime,
            @RequestParam(name = "endDateTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date endDateTime,
            @RequestParam("domains") String[] domains,
            DataTableRequest request
    ) {

        ThresholdsFilterRequest filterRequest = ThresholdsFilterRequest.builder()
                .domains(domains)
                .startDateTime(startDateTime)
                .endDateTime(endDateTime)
                .build();
        Page<PlayerThresholdHistory> pagePlayerThresholdHistory = thresholdHistoryReportService.getPlayerThresholdHistoryPage(filterRequest, request.getPageRequest());

        return new DataTableResponse<>(request, pagePlayerThresholdHistory);
    }

//    @PostMapping("/search")
//    public DataTableResponse<ThresholdHistoryDTO> getThresholdHistoryDTOPage(@RequestBody ThresholdsFilterRequest filterPageRequest, @RequestParam Integer page, @RequestParam Integer size) {
//        DataTableRequest dtRequest = new DataTableRequest();
//        PageRequest pageRequest = PageRequest.of(page, size);
//        dtRequest.setPageRequest(pageRequest);
//        Page<PlayerThresholdHistory> pagePlayerThresholdHistory = thresholdHistoryReportService.getPlayerThresholdHistoryPage(filterPageRequest, pageRequest);
//        Page<ThresholdHistoryDTO> pageTransactionsDTO = pagePlayerThresholdHistory.map(this::buildTransactionDTO);
//        return new DataTableResponse<>(
//                dtRequest,
//                pageTransactionsDTO,
//                pageTransactionsDTO.getTotalElements(),
//                pageTransactionsDTO.getPageable().getPageNumber(),
//                pageTransactionsDTO.getTotalPages()
//        );
//    }
//
//    private ThresholdHistoryDTO buildTransactionDTO(PlayerThresholdHistory transaction) {
//        return mapper.convertValue(transaction, ThresholdHistoryDTO.class);
//    }

}

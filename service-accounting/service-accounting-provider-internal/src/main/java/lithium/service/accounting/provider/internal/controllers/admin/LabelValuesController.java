package lithium.service.accounting.provider.internal.controllers.admin;

import lithium.service.Response;
import lithium.service.accounting.provider.internal.data.entities.LabelValue;
import lithium.service.accounting.provider.internal.services.LabelValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/admin/transactions/labelvalues")
public class LabelValuesController {

    @Autowired
    private LabelValueService labelValueService;

    @GetMapping("/{labelName}")
    Response<Page<LabelValue>> findByLabelName(
            @PathVariable("labelName") String labelName,
            @RequestParam("pageNumber") int pageNumber,
            @RequestParam("fetchSize") int fetchSize) {
        Page<LabelValue> values = labelValueService.getLabelValuesByLabelName(labelName, PageRequest.of(pageNumber, fetchSize, Sort.Direction.ASC, new String[] {"value"}));
        return Response.<Page<LabelValue>>builder().data(values).build();
    }

}

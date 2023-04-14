package lithium.service.games.controllers.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import lithium.service.Response;
import lithium.service.games.client.objects.supplier.GameVerticalEnum;
import lithium.service.games.client.objects.supplier.SupplierGameMetaData;
import lithium.service.games.services.SupplierGameMetaDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/frontend/games")
public class FrontendGamesController {
    @Autowired
    private SupplierGameMetaDataService supplierGameMetaDataService;

    @GetMapping("/meta")
    public Response<List<SupplierGameMetaData>> findGamesMetaData(@RequestParam String provider,
                                                                  @RequestParam String supplier,
                                                                  @RequestParam String domain,
                                                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                                                  @RequestParam(value = "size", defaultValue = "20") int size) {
        GameVerticalEnum gameVerticalEnum = GameVerticalEnum.decode(supplier);
        List<lithium.service.games.data.entities.supplier.SupplierGameMetaData> metaDataList = supplierGameMetaDataService.retrieve(provider, gameVerticalEnum, domain, page, size);
        ObjectMapper mapper = new ObjectMapper();
        List<SupplierGameMetaData> metaDataResultList = new ArrayList<>();
        metaDataList.forEach(metaData -> {
            if(metaData.getGame() == null || metaData.getGame().getSupplierGameGuid() == null || metaData.getGame().getSupplierGameGuid().trim().isEmpty())
                return;
            SupplierGameMetaData metaDataResult = mapper.convertValue(metaData, SupplierGameMetaData.class);
            metaDataResult.setGameGuid(metaData.getGame().getGuid());
            metaDataResultList.add(metaDataResult);
        });
        return Response.<List<SupplierGameMetaData>>builder()
                .data(metaDataResultList)
                .status(Response.Status.OK)
                .build();
    }

}

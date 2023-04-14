package lithium.service.settlement.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.settlement.data.entities.BatchSettlements;
import lithium.service.settlement.data.entities.Settlement;
import lithium.service.settlement.services.SettlementService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/settlements")
@Slf4j
public class SettlementsController {
	@Autowired SettlementService settlementService;
	
	@GetMapping("/{batchSettlementsId}/settlementsTable")
	public DataTableResponse<Settlement> settlementsTable(
		@PathVariable("batchSettlementsId") BatchSettlements batchSettlements,
		DataTableRequest request,
		LithiumTokenUtil tokenUtil
	) {
		return settlementService.settlementsTable(batchSettlements, request);
	}
	
	@GetMapping("/settlement/findbyentity/{batchName}/{entityUuid}/{dateStart}/{dateEnd}")
	public Response<Settlement> findByEntity(
		@PathVariable("batchName") String batchName,
		@PathVariable("entityUuid") String entityUuid,
		@PathVariable("dateStart") String dateStart,
		@PathVariable("dateEnd") String dateEnd,
		LithiumTokenUtil tokenUtil
	) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Settlement s = null;
		try {
			s = settlementService.findByEntity(batchName, entityUuid, sdf.parse(dateStart), sdf.parse(dateEnd));
			return Response.<Settlement>builder().data(s).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Settlement>builder().data(s).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/settlement/findbyuser/{batchName}/{userGuid}/{dateStart}/{dateEnd}")
	public Response<Settlement> findByUser(
		@PathVariable("batchName") String batchName,
		@PathVariable("userGuid") String userGuid,
		@PathVariable("dateStart") String dateStart,
		@PathVariable("dateEnd") String dateEnd,
		LithiumTokenUtil tokenUtil
	) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Settlement s = null;
		try {
			s = settlementService.findByUser(batchName, URLDecoder.decode(userGuid, "UTF-8"), sdf.parse(dateStart), sdf.parse(dateEnd));
			return Response.<Settlement>builder().data(s).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Settlement>builder().data(s).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/create")
	public Response<Settlement> create(
		@RequestBody lithium.service.settlement.client.objects.Settlement settlement,
		LithiumTokenUtil tokenUtil
	) {
		Settlement s = null;
		try {
			s = settlementService.createSettlement(settlement);
			return Response.<Settlement>builder().data(s).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Settlement>builder().data(s).status(INTERNAL_SERVER_ERROR).build();
		}
	}
}

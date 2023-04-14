package lithium.service.machine.controllers;

import static lithium.service.Response.Status.OK;
import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.machine.data.entities.MachineSettlement;
import lithium.service.machine.services.MachinesSettlementService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/machines/settlements")
@Slf4j
public class MachinesSettlementsController {
	@Autowired MachinesSettlementService service;
	
	@GetMapping("/table")
	public DataTableResponse<MachineSettlement> table(
		DataTableRequest request,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		return service.table(request, tokenUtil.playerDomainWithRole("MACHINES_MANAGE").getName());
	}
	
	@PostMapping("/create")
	public Response<MachineSettlement> create(
		@RequestParam("batchName") String batchName,
		@RequestParam("dateStart") @DateTimeFormat(iso=ISO.DATE_TIME) Date dateStart,
		@RequestParam("dateEnd") @DateTimeFormat(iso=ISO.DATE_TIME) Date dateEnd,
		LithiumTokenUtil tokenUtil
	) {
		MachineSettlement job = null;
		try {
			job = service.create(
					tokenUtil.playerDomainWithRole("MACHINES_MANAGE").getName(),
					batchName, dateStart, dateEnd, tokenUtil.guid());
			return Response.<MachineSettlement>builder().data(job).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<MachineSettlement>builder().data(job).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
}

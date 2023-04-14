package lithium.service.machine.controllers;

import static lithium.service.Response.Status.OK;
import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.machine.data.entities.MachineSettlement;
import lithium.service.machine.services.MachinesSettlementService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/machines/settlement/{id}")
@Slf4j
public class MachinesSettlementController {
	@Autowired MachinesSettlementService service;
	
	@GetMapping
	public Response<MachineSettlement> get(
		@PathVariable("id") MachineSettlement machineSettlement,
		LithiumTokenUtil tokenUtil
	) {
		return Response.<MachineSettlement>builder().data(machineSettlement).status(OK).build();
	}
	
	@PostMapping("/rerun")
	public Response<MachineSettlement> rerun(
		@PathVariable("id") MachineSettlement machineSettlement,
		LithiumTokenUtil tokenUtil
	) {
		MachineSettlement ms = null;
		try {
			ms = service.rerun(machineSettlement);
			return Response.<MachineSettlement>builder().data(ms).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<MachineSettlement>builder().data(ms).status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
		}
	}
}

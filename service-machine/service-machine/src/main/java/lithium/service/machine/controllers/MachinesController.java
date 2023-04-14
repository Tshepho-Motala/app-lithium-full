package lithium.service.machine.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.machine.data.entities.Machine;
import lithium.service.machine.data.entities.Status;
import lithium.service.machine.data.objects.Distribution;
import lithium.service.machine.data.repositories.MachineRepository;
import lithium.service.machine.services.DomainService;
import lithium.service.machine.services.MachineService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/machines")
@Slf4j
public class MachinesController {
	@Autowired DomainService domainService;
	@Autowired MachineService machineService;
	@Autowired MachineRepository repo;
	
	@GetMapping("/report/table/{domainName}")
	public DataTableResponse<Machine> reportTable(
		DataTableRequest request,
		@PathVariable("domainName") String domainName
	) {
		return machineService.table(domainName, request, null);
	}
	
	@GetMapping("/table")
	public DataTableResponse<Machine> table(
		DataTableRequest request, 
		LithiumTokenUtil tokenUtil,
		@RequestParam(name="statusId", required=false) Status status
	) throws Exception {
		return machineService.table(tokenUtil.playerDomainWithRole("MACHINES_MANAGE").getName(), request, status);
	}
	
	@PostMapping
	public Response<Machine> create(
		@RequestBody lithium.service.machine.client.objects.Machine machine,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		Machine m = null;
		try {
			m = machineService.createMachine(machine, tokenUtil);
			return Response.<Machine>builder().data(m).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Machine>builder().data(m).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/machine/distribution")
	public Response<Distribution> getMachineDistribution(
		@RequestParam(required=false, name="machineId") Long machineId,
		@RequestParam(required=false, name="machineGuid") String machineGuid,
		@RequestParam("date") @DateTimeFormat(iso=ISO.DATE_TIME) Date date,
		LithiumTokenUtil tokenUtil
	) throws Exception {
		return Response.<Distribution>builder()
			.data(machineService.getMachineDistribution(machineId, machineGuid, date, tokenUtil.playerDomainWithRole("MACHINES_MANAGE").getName()))
			.status(OK)
			.build();
	}
	
}
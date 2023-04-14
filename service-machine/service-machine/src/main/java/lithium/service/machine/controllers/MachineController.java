package lithium.service.machine.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.machine.data.entities.LocationDistributionConfigurationRevision;
import lithium.service.machine.data.entities.Machine;
import lithium.service.machine.data.entities.Relationship;
import lithium.service.machine.data.entities.RelationshipDistributionConfigurationRevision;
import lithium.service.machine.services.MachineService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/machine/{id}")
@Slf4j
public class MachineController {
	@Autowired MachineService machineService;
	@Autowired ChangeLogService changelogService;
	
	@GetMapping
	public Response<Machine> get(
		@PathVariable("id") Machine machine,
		Authentication authentication
	) {
		return Response.<Machine>builder().data(machine).status(OK).build();
	}
	
	@PutMapping
	public Response<Machine> save(
		@PathVariable("id") Machine machine,
		@RequestBody lithium.service.machine.client.objects.Machine machinePost,
		Principal principal
	) throws Exception {
		Machine m = null;
		try {
			m = machineService.saveMachine(machine, machinePost, principal);
			return Response.<Machine>builder().data(m).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Machine>builder().data(m).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping(value="/status/save")
	public Response<Machine> saveStatus(
		@RequestBody lithium.service.machine.client.objects.StatusUpdate statusUpdate,
		Principal principal
	) throws Exception {
		Machine m = null;
		try {
			m = machineService.saveStatus(statusUpdate, principal);
			return Response.<Machine>builder().status(OK).data(m).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Machine>builder().status(INTERNAL_SERVER_ERROR).data(m).build();
		}
	}
	
	@PostMapping(value = "/location/add")
	public Response<Machine> addLocation(
		@PathVariable("id") Machine machine,
		@RequestBody lithium.service.machine.client.objects.Location location,
		Principal principal
	) {
		Machine m = null;
		try {
			m = machineService.addLocation(machine, location, principal);
			return Response.<Machine>builder().status(OK).data(m).build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return Response.<Machine>builder().status(INTERNAL_SERVER_ERROR).data(m).build();
		}
	}
	
	@PostMapping(value = "/location/save")
	public Response<Machine> saveLocation(
		@PathVariable("id") Machine machine,
		@RequestBody lithium.service.machine.client.objects.Location location,
		Principal principal
	) {
		Machine m = null;
		try {
			m = machineService.saveLocation(machine, location, principal);
			return Response.<Machine>builder().status(OK).data(m).build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return Response.<Machine>builder().status(INTERNAL_SERVER_ERROR).data(m).build();
		}
	}
	
	@PostMapping(value = "/location/delete")
	public Response<Machine> removeLocation(
		@PathVariable("id") Machine machine,
		Principal principal
	) {
		Machine m = null;
		try {
			m = machineService.deleteLocation(machine, principal);
			return Response.<Machine>builder().status(OK).data(m).build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return Response.<Machine>builder().status(INTERNAL_SERVER_ERROR).data(m).build();
		}
	}
	
	@PostMapping("/location/distconfig/history/{configId}/modify")
	public Response<Boolean> locationDistConfigHistoryModify(
		@PathVariable("id") Machine machine,
		@PathVariable("configId") LocationDistributionConfigurationRevision rev,
		@RequestParam("percentage") BigDecimal percentage,
		@RequestParam(value="start", required=false) @DateTimeFormat(iso=ISO.DATE_TIME) Date start,
		@RequestParam(value="end", required=false) @DateTimeFormat(iso=ISO.DATE_TIME) Date end,
		Principal principal
		
	) throws Exception {
		machineService.modifyLocationDistConfigRevisionHistory(machine, rev, percentage, start, end, principal);
		return Response.<Boolean>builder().data(true).status(OK).build();
	}
	
	@PostMapping("/location/distconfig/history/{configId}/delete")
	public Response<Machine> locationDistConfigHistoryDelete(
		@PathVariable("id") Machine machine,
		@PathVariable("configId") LocationDistributionConfigurationRevision rev,
		Principal principal
	) throws Exception {
		Machine m = null;
		try {
			m = machineService.deleteLocationDistConfigRevisionHistory(machine, rev, principal);
			return Response.<Machine>builder().data(m).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Machine>builder().data(m).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/location/history")
	public DataTableResponse<LocationDistributionConfigurationRevision> locationHistory(
		@PathVariable("id") Machine machine,
		DataTableRequest request,
		Principal principal
	) {
		return machineService.locationHistory(machine, request, principal);
	}
	
	@PostMapping(value = "/relationship/add")
	public Response<Machine> addRelationship(
		@PathVariable("id") Machine machine,
		@RequestBody lithium.service.machine.client.objects.Relationship r,
		Principal principal
	) {
		Machine m = null;
		try {
			m = machineService.addRelationship(machine, r, principal);
			return Response.<Machine>builder().status(OK).data(m).build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return Response.<Machine>builder().status(INTERNAL_SERVER_ERROR).data(m).build();
		}
	}
	
	@PostMapping(value = "/relationship/{relationshipId}/save")
	public Response<Machine> saveRelationship(
		@PathVariable("id") Machine machine,
		@PathVariable("relationshipId") Relationship relationship,
		@RequestBody lithium.service.machine.client.objects.Relationship r,
		Principal principal
	) {
		Machine m = null;
		try {
			m = machineService.saveRelationship(machine, relationship, r, principal);
			return Response.<Machine>builder().status(OK).data(m).build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return Response.<Machine>builder().status(INTERNAL_SERVER_ERROR).data(m).build();
		}
	}
	
	@PostMapping(value = "/relationship/{relationshipId}/delete")
	public Response<Machine> deleteRelationship(
		@PathVariable("id") Machine machine,
		@PathVariable("relationshipId") Relationship relationship,
		Principal principal
	) {
		Machine m = null;
		try {
			m = machineService.deleteRelationship(machine, relationship, principal);
			return Response.<Machine>builder().status(OK).data(m).build();
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			return Response.<Machine>builder().status(INTERNAL_SERVER_ERROR).data(m).build();
		}
	}
	
	@PostMapping("/relationship/distconfig/history/{configId}/modify")
	public Response<Boolean> relationshipHistoryModify(
		@PathVariable("id") Machine machine,
		@PathVariable("configId") RelationshipDistributionConfigurationRevision rev,
		@RequestParam("percentage") BigDecimal percentage,
		@RequestParam(value="start", required=false) @DateTimeFormat(iso=ISO.DATE_TIME) Date start,
		@RequestParam(value="end", required=false) @DateTimeFormat(iso=ISO.DATE_TIME) Date end,
		Principal principal
	) throws Exception {
		machineService.modifyRelationshipDistConfigRevisionHistory(machine, rev, percentage, start, end, principal);
		return Response.<Boolean>builder().data(true).status(OK).build();
	}
	
	@PostMapping("/relationship/distconfig/history/{configId}/delete")
	public Response<Machine> relationshipDistConfigHistoryDelete(
		@PathVariable("id") Machine machine,
		@PathVariable("configId") RelationshipDistributionConfigurationRevision rev,
		Principal principal
	) throws Exception {
		Machine m = null;
		try {
			m = machineService.deleteRelationshipDistConfigRevisionHistory(machine, rev, principal);
			return Response.<Machine>builder().data(m).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Machine>builder().data(m).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/relationship/history")
	public DataTableResponse<RelationshipDistributionConfigurationRevision> relationshipHistory(
		@PathVariable("id") Machine machine,
		DataTableRequest request,
		Principal principal
	) {
		return machineService.relationshipHistory(machine, request, principal);
	}
	
	@GetMapping(value = "/changelogs")
	public @ResponseBody Response<ChangeLogs> changeLogs(
		@PathVariable Long id, 
		@RequestParam int p
	) throws Exception {
		return changelogService.listLimited(
			ChangeLogRequest.builder()
				.entityRecordId(id)
				.entities(new String[] { "machine" })
				.page(p)
				.build()
		);
	}
}
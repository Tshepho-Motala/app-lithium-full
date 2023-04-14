package lithium.service.machine.services;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.machine.data.entities.Domain;
import lithium.service.machine.data.repositories.MachineRepository;

@Service
public class MachineStatsService {
	
	@Autowired MachineRepository machineRepo;
	@Autowired StatusService statusService;

	public Integer getActiveMachines(Domain domain) {
		return machineRepo.countByDomainAndStatus(domain, statusService.ACTIVE);
	}
	
	public Integer getOnlineMachines(Domain domain) {
		return machineRepo.countByDomainAndStatusAndLastPingGreaterThan(domain, statusService.ACTIVE, new DateTime().minusSeconds(120).toDate());
	}
	
}
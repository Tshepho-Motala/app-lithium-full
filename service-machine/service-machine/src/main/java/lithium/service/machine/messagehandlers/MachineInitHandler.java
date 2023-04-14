package lithium.service.machine.messagehandlers;

import java.util.Date;

import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lithium.service.machine.client.objects.MachineInitRequest;
import lithium.service.machine.client.objects.MachineInitResponse;
import lithium.service.machine.client.objects.Status;
import lithium.service.machine.data.entities.Domain;
import lithium.service.machine.data.entities.Machine;
import lithium.service.machine.data.entities.MachineObserver;
import lithium.service.machine.data.repositories.MachineObserverRepository;
import lithium.service.machine.data.repositories.MachineRepository;
import lithium.service.machine.services.DomainService;
import lithium.service.machine.services.StatusService;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MachineInitHandler {
	
	@Autowired MachineRepository repo;
	@Autowired DomainService domainService;
	@Autowired StatusService statusService;
	@Autowired MachineObserverRepository observerRepo;

	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "machineinit", durable = "false"),
			exchange = @Exchange(value = "machineinit"),
			key = "machineinit"))
	public MachineInitResponse machineInit(MachineInitRequest request) throws InterruptedException {
		log.info("Received machine init: " + request.toString());
		Domain domain = domainService.findOrCreate(request.getDomain());
		Machine machine = repo.findByDomainAndGuid(domain, request.getMachineGuid());
		if (machine == null) {
			machine = Machine.builder()
					.createdDate(new Date())
					.domain(domain)
					.guid(request.getMachineGuid())
					.status(statusService.NEW)
					.build();
		}
		machine.setLastPing(new Date());
		repo.save(machine);
		
		// We are a special observer, we are both the machine being observed and the observer
		MachineObserver o = observerRepo.findByMachineAndObserverGuid(machine, machine.getGuid());
		if (o == null) o = new MachineObserver();
		o.setGatewayQueue(request.getGatewayQueue());
		o.setSocketSessionId(request.getSocketSessionId());
		o.setMachine(machine);
		o.setObserverGuid(machine.getGuid());
		observerRepo.save(o);
		
		return MachineInitResponse.builder()
				.guid(machine.getGuid())
				.requestTimestamp(request.getRequestTimestamp())
				.responseTimestamp(new Date().getTime())
				.status(Status.builder()
						.enabled(machine.getStatus().getEnabled())
						.name(machine.getStatus().getName()).build())
				.build();
	}

}

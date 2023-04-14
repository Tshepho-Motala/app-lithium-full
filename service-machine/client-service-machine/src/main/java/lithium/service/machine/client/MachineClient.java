package lithium.service.machine.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lithium.service.client.datatable.DataTableResponse;
import lithium.service.machine.client.objects.Machine;

@FeignClient(name="service-machine")
public interface MachineClient {
	@RequestMapping(path = "/machines/report/table/{domainName}") 
	public DataTableResponse<Machine> table(@RequestParam("draw") String drawEcho, @RequestParam("start") Long start, @RequestParam("length") Long length, @PathVariable("domainName") String domainName);
}

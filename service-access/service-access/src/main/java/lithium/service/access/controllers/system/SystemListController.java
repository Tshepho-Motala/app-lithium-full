package lithium.service.access.controllers.system;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.access.data.entities.List;
import lithium.service.access.data.entities.Value;
import lithium.service.access.services.ListService;
import lithium.service.access.services.ListValueService;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/system/list/{id}")
public class SystemListController {

  @Autowired ListService listService;
  @Autowired ListValueService listValueService;

  @PostMapping("/add-data-value")
  public Response<List> addListValue(@PathVariable("id") List list, @RequestBody String data, LithiumTokenUtil tokenUtil) throws Exception {
    return listService.addListValue(list, data, tokenUtil);
  }

  @PostMapping("/remove-data-value")
  public Response<List> removeListDataValue(@PathVariable("id") List list, @RequestBody String data, LithiumTokenUtil tokenUtil) throws Exception {
    Value value = listValueService.findValue(list, data);
    if (value != null) {
      return listService.removeListValue(list, value.getId(), tokenUtil);
    }
    return Response.<List>builder().status(Status.NOT_FOUND).build();
  }

}

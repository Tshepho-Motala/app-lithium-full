package lithium.service.access.services;

import lithium.client.changelog.Category;
import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.SubCategory;
import lithium.client.changelog.objects.ChangeLogFieldChange;
import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.access.data.entities.List;
import lithium.service.access.data.entities.Value;
import lithium.service.access.data.repositories.ListRepository;
import lithium.service.access.data.repositories.ValueRepository;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
public class ListService {

  @Autowired ListValueService listValueService;
  @Autowired ChangeLogService changeLogService;
  @Autowired ListRepository listRepository;
  @Autowired ValueRepository valueRepository;

  public Response<List> addListValue(List list, String data, LithiumTokenUtil tokenUtil) {
    try {
      java.util.List<Value> oldValues = new ArrayList<>();
      list.getValues().stream().forEach(value -> {
        oldValues.add(value);
      });
      Value value = listValueService.addValue(list, data);
      if (value == null) return Response.<List>builder().status(Status.CONFLICT).build();
      list = listRepository.findOne(list.getId());
      java.util.List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
      ChangeLogFieldChange c = ChangeLogFieldChange.builder()
          .field("data")
          .fromValue("")
          .toValue(data)
          .build();
      clfc.add(c);
      changeLogService.registerChangesWithDomain("list.value", "create", list.getId(), tokenUtil.guid(), null, null, clfc, Category.ACCESS,
          SubCategory.ACCESS_RULE, 0, list.getDomain().getName());
      return Response.<List>builder().data(list).status(Status.OK).build();
    } catch (Exception e) {
      return Response.<List>builder().status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  public Response<List> removeListValue(List list, Long valueId, LithiumTokenUtil tokenUtil) throws Exception {
    try {
      java.util.List<Value> oldValues = new ArrayList<Value>();
      list.getValues().stream().forEach(value -> {
        oldValues.add(value);
      });
      Value value = valueRepository.findOne(valueId);
      list = listValueService.removeValue(list, value);
      java.util.List<ChangeLogFieldChange> clfc = new ArrayList<ChangeLogFieldChange>();
      ChangeLogFieldChange c = ChangeLogFieldChange.builder()
          .field("data")
          .fromValue(value.getData())
          .toValue("")
          .build();
      clfc.add(c);
      changeLogService.registerChangesWithDomain("list.value", "delete", list.getId(), tokenUtil.guid(), null, null, clfc, Category.ACCESS,
          SubCategory.ACCESS_RULE, 0, list.getDomain().getName());
      return Response.<List>builder().data(list).status(Status.OK).build();
    } catch (Exception e) {
      return Response.<List>builder().status(Status.INTERNAL_SERVER_ERROR).build();
    }
  }
}

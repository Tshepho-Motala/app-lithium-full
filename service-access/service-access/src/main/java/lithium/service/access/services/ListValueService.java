package lithium.service.access.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.access.data.entities.List;
import lithium.service.access.data.entities.Value;
import lithium.service.access.data.repositories.ListRepository;
import lithium.service.access.data.repositories.ValueRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ListValueService {
	@Autowired ValueRepository valueRepository;
	@Autowired ListRepository listRepository;
	
	public Value addValue(List list, String data) {
		boolean exists = (valueRepository.findByListAndDataIgnoreCase(list, data) != null);
		if (!exists) {
			Value value = Value.builder()
			.data(data)
			.list(list)
			.build();
			log.debug(""+value);
			value = valueRepository.save(value);
			log.debug("Saved Value: " + value);
			list.getValues().add(value);
			listRepository.save(list);
			return value;
		}
		return null;
	}
	
	public List removeValue(List list, Value value) {
		list.getValues().remove(value);
		list = listRepository.save(list);
		valueRepository.delete(value);
		return list;
	}

  public Value findValue(List list, String data) {
    return valueRepository.findByListAndDataIgnoreCase(list, data);
  }
}

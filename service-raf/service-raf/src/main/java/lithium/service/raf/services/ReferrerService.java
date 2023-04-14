package lithium.service.raf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.raf.data.entities.Referrer;
import lithium.service.raf.data.repositories.ReferrerRepository;

@Service
public class ReferrerService {
	@Autowired ReferrerRepository repository;
	
	public Referrer findOrCreate(String playerGuid) {
		Referrer referrer = repository.findByPlayerGuid(playerGuid);
		if (referrer == null) referrer = repository.save(Referrer.builder().playerGuid(playerGuid).build());
		return referrer;
	}
}

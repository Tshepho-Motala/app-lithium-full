package lithium.service.document.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lithium.service.document.data.entities.Owner;
import lithium.service.document.data.repositories.OwnerRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OwnerService {
	@Autowired
	private OwnerRepository ownerRepo;

	public Owner findOrCreateOwner(String ownerGuid) {
		Owner owner = ownerRepo.findByGuid(ownerGuid);
		
		if (owner == null) {
			owner = ownerRepo.save(
				Owner.builder()
				.guid(ownerGuid)
				.build()
			);
		}
		
		return owner;
	}

}

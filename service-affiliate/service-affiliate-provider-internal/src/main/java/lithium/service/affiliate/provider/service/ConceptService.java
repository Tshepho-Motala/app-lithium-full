package lithium.service.affiliate.provider.service;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import lithium.service.affiliate.provider.data.entities.Concept;
import lithium.service.affiliate.provider.data.entities.ConceptType;
import lithium.service.affiliate.provider.data.repositories.ConceptRepository;
import lithium.service.affiliate.provider.data.repositories.ConceptTypeRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ConceptService {
	@Autowired LithiumServiceClientFactory services;
	@Autowired ConceptRepository conceptRepository;
	@Autowired ConceptTypeRepository conceptTypeRepository;
	@Autowired TokenStore tokenStore;

	public Concept findOrCreate(Concept concept) {
		Concept repoConcept = conceptRepository.findByName(concept.getName());
		
		if (repoConcept == null) {
			repoConcept = conceptRepository.save(Concept.builder().name(concept.getName()).type(concept.getType()).build());
		}
		return repoConcept;
	}
	
	public ConceptType findOrCreate(ConceptType conceptType) {
		ConceptType repoConceptType = conceptTypeRepository.findByName(conceptType.getName());
		
		if (repoConceptType == null) {
			repoConceptType = conceptTypeRepository.save(ConceptType.builder().name(conceptType.getName()).build());
		}
		return repoConceptType;
	}
	
	public List<ConceptType> listConceptTypes() {
		return conceptTypeRepository.listOrderByName();
	}
	
	public List<Concept> listConcepts() {
		return conceptRepository.listOrderByName();
	}

	public Concept edit(Concept concept) {
		Concept repoConcept = conceptRepository.findOne(concept.getId());
		
		repoConcept.setName(concept.getName());
		repoConcept.setType(concept.getType());
		
		repoConcept = conceptRepository.save(repoConcept);
		
		return repoConcept;
	}
	
	public ConceptType edit(ConceptType conceptType) {
		ConceptType repoConceptType = conceptTypeRepository.findOne(conceptType.getId());
		
		repoConceptType.setName(conceptType.getName());
		
		repoConceptType = conceptTypeRepository.save(repoConceptType);
		
		return repoConceptType;
	}
	

//	public AffiliateClient getAffiliateClient() {
//		AffiliateClient client = null;
//		try {
//			client = services.target(AffiliateClient.class, "service-user", true);
//		} catch (LithiumServiceClientFactoryException e) {
//			log.error(e.getMessage(), e);
//		}
//		return client;
//	}
	
//	public LithiumTokenUtil getTokenUtil(Principal principal) {
//		return LithiumTokenUtil.builder(tokenStore, principal).build();
//	}
}

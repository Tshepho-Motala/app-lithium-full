package lithium.service.affiliate.provider.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import lithium.service.affiliate.provider.data.entities.Contract;
import lithium.service.affiliate.provider.data.entities.ContractType;
import lithium.service.affiliate.provider.data.repositories.ContractRepository;
import lithium.service.affiliate.provider.data.repositories.ContractTypeRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ContractService {
	@Autowired LithiumServiceClientFactory services;
	@Autowired ContractRepository contractRepository;
	@Autowired ContractTypeRepository contractTypeRepository;
	@Autowired ContractPaymentScaleService contractPaymentScaleService;
	@Autowired TokenStore tokenStore;

//	public Contract create(Contract contract) {
//		
//		if (contract.isDefaultContract()) {
//			Contract defaultContract = contractRepository.findByTypeAndDefaultContractTrue(contract.getType());
//			if (defaultContract != null) {
//				defaultContract.setDefaultContract(false);
//				defaultContract = contractRepository.save(defaultContract);
//			}
//		}
//		
//		Contract repoContract = contractRepository.save(contract);
//
//		return repoContract;
//	}
//	
//	public List<Contract> findByType(ContractType contractType) {
//		return contractRepository.findByTypeOrderByCreationDateDesc(contractType);
//	}
//	
//	public ContractType findOrCreate(ContractType contractType) {
//		ContractType repoContractType = contractTypeRepository.findByName(contractType.getName());
//		
//		if (repoContractType == null) {
//			repoContractType = contractTypeRepository.save(contractType);
//		}
//		return repoContractType;
//	}
//	
//	public List<ContractType> listContractTypes() {
//		return (List<ContractType>) contractTypeRepository.findAll();
//	}
}

package lithium.service.affiliate.provider.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import lithium.service.affiliate.provider.data.entities.Contract;
import lithium.service.affiliate.provider.data.entities.PaymentScale;
import lithium.service.affiliate.provider.data.repositories.ContractPaymentScaleRepository;
import lithium.service.client.LithiumServiceClientFactory;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class ContractPaymentScaleService {
//	@Autowired LithiumServiceClientFactory services;
//	@Autowired ContractPaymentScaleRepository contractPaymentScaleRepository;
//	@Autowired TokenStore tokenStore;
//
//	public List<PaymentScale> create(List<PaymentScale> contractPaymentScaleList) {
//		
//		if (contractPaymentScaleList.isEmpty()) {
//			log.error("An attempt to write an empty payment scale was rejected");
//			return null;
//		}
//		
//		Contract contract = contractPaymentScaleList.get(0).getContract();
//		
//		if (contract == null || contract.getId() == 0L) {
//			log.error("An attempt to write a payment scale with no bound contract: " + contract + " scale: " + contractPaymentScaleList);
//			return null;
//		}
//		List<PaymentScale> repoContractPaymentScaleList = contractPaymentScaleRepository.findByContract(contract);
//		
//		if (repoContractPaymentScaleList == null || repoContractPaymentScaleList.isEmpty()) {
//			repoContractPaymentScaleList = (List<PaymentScale>) contractPaymentScaleRepository.save(contractPaymentScaleList);
//		} else {
//			log.error("An attempt to write a payment scale for a contract with an existing scale: " + contract + " scale: " + contractPaymentScaleList + " repo scale: " + repoContractPaymentScaleList);
//			return null;
//		}
//		
//		return repoContractPaymentScaleList;
//	}
//	
//	public List<PaymentScale> list(Contract contract) {
//		List<PaymentScale> repoContractPaymentScaleList = contractPaymentScaleRepository.findByContract(contract);
//		
//		if (repoContractPaymentScaleList == null || repoContractPaymentScaleList.isEmpty()) {
//			log.warn("An attempt to retrieve a payment scale for a contract but the scale does not exist on contract: " + contract);
//		}
//		
//		return repoContractPaymentScaleList;
//	}
}

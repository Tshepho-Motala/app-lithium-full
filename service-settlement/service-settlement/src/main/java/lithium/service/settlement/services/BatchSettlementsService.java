package lithium.service.settlement.services;

import lithium.report.XlsReport;
import lithium.service.client.LithiumServiceClientFactory;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.domain.client.DomainClient;
import lithium.service.settlement.credtransfinitv3.xml.AccountIdentification4Choice;
import lithium.service.settlement.credtransfinitv3.xml.AccountSchemeName1Choice;
import lithium.service.settlement.credtransfinitv3.xml.ActiveOrHistoricCurrencyAndAmount;
import lithium.service.settlement.credtransfinitv3.xml.AmountType3Choice;
import lithium.service.settlement.credtransfinitv3.xml.BranchAndFinancialInstitutionIdentification4;
import lithium.service.settlement.credtransfinitv3.xml.CashAccount16;
import lithium.service.settlement.credtransfinitv3.xml.CreditTransferTransactionInformation10;
import lithium.service.settlement.credtransfinitv3.xml.CustomerCreditTransferInitiationV03;
import lithium.service.settlement.credtransfinitv3.xml.Document;
import lithium.service.settlement.credtransfinitv3.xml.EquivalentAmount2;
import lithium.service.settlement.credtransfinitv3.xml.FinancialInstitutionIdentification7;
import lithium.service.settlement.credtransfinitv3.xml.GenericAccountIdentification1;
import lithium.service.settlement.credtransfinitv3.xml.GenericOrganisationIdentification1;
import lithium.service.settlement.credtransfinitv3.xml.GroupHeader32;
import lithium.service.settlement.credtransfinitv3.xml.OrganisationIdentification4;
import lithium.service.settlement.credtransfinitv3.xml.OrganisationIdentificationSchemeName1Choice;
import lithium.service.settlement.credtransfinitv3.xml.Party6Choice;
import lithium.service.settlement.credtransfinitv3.xml.PartyIdentification32;
import lithium.service.settlement.credtransfinitv3.xml.PaymentIdentification1;
import lithium.service.settlement.credtransfinitv3.xml.PaymentInstructionInformation3;
import lithium.service.settlement.credtransfinitv3.xml.PaymentMethod3Code;
import lithium.service.settlement.credtransfinitv3.xml.PostalAddress6;
import lithium.service.settlement.credtransfinitv3.xml.RemittanceInformation5;
import lithium.service.settlement.data.entities.Address;
import lithium.service.settlement.data.entities.BatchSettlements;
import lithium.service.settlement.data.entities.Domain;
import lithium.service.settlement.data.entities.Settlement;
import lithium.service.settlement.data.entities.SettlementEntry;
import lithium.service.settlement.data.repositories.BatchSettlementsRepository;
import lithium.service.settlement.data.specifications.BatchSettlementsSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class BatchSettlementsService {
	@Autowired BatchSettlementsRepository repo;
	@Autowired DomainService domainService;
	@Autowired SettlementService settlementService;
	@Autowired LithiumServiceClientFactory services;
	@Autowired ExternalEntityService externalEntityService;
	@Autowired ExternalUserService externalUserService;
	
	public DataTableResponse<BatchSettlements> table(DataTableRequest request, List<String> domains) {
		Specification<BatchSettlements> spec = Specification.where(BatchSettlementsSpecifications.domains(domains));
		
		if ((request.getSearchValue() != null) && (request.getSearchValue().length() > 0)) {
			Specification<BatchSettlements> s = Specification.where(BatchSettlementsSpecifications.any(request.getSearchValue()));
			spec = (spec == null)? s: spec.and(s);
		}
		
		Page<BatchSettlements> pageList = repo.findAll(spec, request.getPageRequest());
		
		return new DataTableResponse<>(request, pageList);
	}
	
	public boolean batchNameIsUnique(String domainName, String batchName) {
		return !(repo.findByNameIgnoreCaseAndDomainName(batchName, domainName) != null);
	}
	
	public BatchSettlements find(String domainName, String batchName) {
		return repo.findByNameIgnoreCaseAndDomainName(batchName, domainName);
	}
	
	public BatchSettlements findOrCreate(String domainName, String batchName) {
		Domain domain = domainService.findOrCreate(domainName);
		BatchSettlements batchSettlements = repo.findByNameIgnoreCaseAndDomainName(batchName, domain.getName());
		if (batchSettlements == null) {
			batchSettlements = BatchSettlements.builder()
			.name(batchName)
			.domain(domain)
			.createdDate(new Date())
			.build();
			batchSettlements = repo.save(batchSettlements);
		} else if (!batchSettlements.getOpen()) {
			batchSettlements = BatchSettlements.builder()
			.name(batchName + " (" + System.currentTimeMillis() + ")")
			.domain(domain)
			.createdDate(new Date())
			.build();
			batchSettlements = repo.save(batchSettlements);
		}
		return batchSettlements;
	}
	
	public BatchSettlements save(BatchSettlements batchSettlements) {
		return repo.save(batchSettlements);
	}
	
	@Transactional(rollbackOn=Exception.class)
	public BatchSettlements finalizeBatchSettlements(BatchSettlements batchSettlements) throws Exception {
		if (batchSettlements.getRerunning())
			throw new Exception("Batch settlements is rerunning");
		for (Settlement settlement: batchSettlements.getSettlements()) {
			if (settlement.getOpen()) {
				settlement = settlementService.finalizeSettlement(settlement);
			}
		}
		batchSettlements.setOpen(false);
		batchSettlements = repo.save(batchSettlements);
		return batchSettlements;
	}
	
	@Transactional(rollbackOn=Exception.class)
	public BatchSettlements initBatchRerun(String domainName, String batchName) {
		BatchSettlements batchSettlements = repo.findByNameIgnoreCaseAndDomainName(batchName, domainName);
		batchSettlements.setRerunning(true);
		batchSettlements.setOpen(true);
		batchSettlements = repo.save(batchSettlements);
		settlementService.deleteSettlements(domainName, batchName);
		return batchSettlements;
	}
	
	public BatchSettlements closeBatchRerun(String domainName, String batchName) {
		BatchSettlements batchSettlements = repo.findByNameIgnoreCaseAndDomainName(batchName, domainName);
		batchSettlements.setRerunning(false);
		batchSettlements = repo.save(batchSettlements);
		return batchSettlements;
	}
	
	
	
	public void exportToXls(BatchSettlements batchSettlements, OutputStream os) throws IOException {
		XlsReport report = new XlsReport(batchSettlements.getId() + "-" + batchSettlements.getName());
		report.run(os, () -> {
			report.sheet("data", () -> {
				report.columnHeading("Date Start");
				report.columnHeading("Date End");
				report.columnHeading("Created By");
				report.columnHeading("Total");
				report.columnHeading("Account Number");
				report.columnHeading("Payment To");
			}, () -> {
				batchSettlements.getSettlements().forEach(settlement -> {
					report.row(() -> {
						
						Settlement s = settlementService.enrichDataWithExternalUserOrEntity(settlement);
						
						String paymentTo = (s.getExternalEntity() != null) ? s.getExternalEntity().getName() 
							: s.getExternalUser().getFirstName() + " " + s.getExternalUser().getLastName();
						
						report.cellDate(settlement.getDateStart());
						report.cellDate(settlement.getDateEnd());
						report.cell(settlement.getCreatedBy());
						report.cellNumeric(settlement.getTotal().doubleValue());
						report.cell(settlement.getBankDetails().getAccountNumber());
						report.cell(paymentTo);
						
					});
				});
			});
		});
	}
	
	public Document exportToNets(BatchSettlements batchSettlements) throws DatatypeConfigurationException, LithiumServiceClientFactoryException {
		DomainClient domainClient = services.target(DomainClient.class, "service-domain", true);
		lithium.service.domain.client.objects.Domain domain = domainClient.findByName(batchSettlements.getDomain().getName()).getData().getParent();
		
		Long totalEntries = 0L;
		BigDecimal cntrlSum = new BigDecimal(0);
		for (Settlement s: batchSettlements.getSettlements()) {
			totalEntries += s.getSettlementEntries().size();
			cntrlSum = cntrlSum.add(s.getTotal());
		}
		
		Date date = new Date();
		
		CustomerCreditTransferInitiationV03 custCreditTransInit = new CustomerCreditTransferInitiationV03();
		
		GroupHeader32 groupHeader = new GroupHeader32();
		groupHeader.setMsgId(batchSettlements.getId().toString());
		groupHeader.setCreDtTm(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").format(date));
		groupHeader.setNbOfTxs(String.valueOf(totalEntries));
		groupHeader.setCtrlSum(cntrlSum);
		
		PartyIdentification32 initParty = new PartyIdentification32();
		initParty.setNm((domain.getBankingDetails() != null) ? domain.getBankingDetails().getAccountHolder() : "");
		Party6Choice identification = new Party6Choice();
		OrganisationIdentification4 orgIdentification = new OrganisationIdentification4();
		GenericOrganisationIdentification1 genericOrgId = new GenericOrganisationIdentification1();
		genericOrgId.setId((domain.getBankingDetails() != null) ? (domain.getBankingDetails().getOrgId() != null) ? domain.getBankingDetails().getOrgId() : ""  : "");
		OrganisationIdentificationSchemeName1Choice schemeNameChoice = new OrganisationIdentificationSchemeName1Choice();
		schemeNameChoice.setCd("CUST");
		genericOrgId.setSchmeNm(schemeNameChoice);
		orgIdentification.getOthr().add(genericOrgId);
		identification.setOrgId(orgIdentification);
		initParty.setId(identification);
		
		groupHeader.setInitgPty(initParty);
		
		custCreditTransInit.setGrpHdr(groupHeader);
		
		for (Settlement settlement: batchSettlements.getSettlements()) {
			settlement = settlementService.enrichDataWithExternalUserOrEntity(settlement);
			
			String paymentTo = (settlement.getExternalEntity() != null) ? settlement.getExternalEntity().getName() 
				: settlement.getExternalUser().getFirstName() + " " + settlement.getExternalUser().getLastName();
			
			Address billingAddress = settlement.getBillingAddress();
			Address physicalAddress = settlement.getPhysicalAddress();
			
			String strName = null;
			String postCode = null;
			String townName = null;
			String country = null;
			String addressLine1 = null;
			String addressLine2 = null;
			String addressLine3 = null;
			
			if (billingAddress != null) {
				strName = (billingAddress.getAddressLine1() != null) ? billingAddress.getAddressLine1() : null;
				postCode = (billingAddress.getPostalCode() != null) ? billingAddress.getPostalCode() : null;
				townName = (billingAddress.getAdminLevel1() != null) ? billingAddress.getAdminLevel1() : null;
				country = (billingAddress.getCountry() != null) ? billingAddress.getCountry() : null;
				addressLine1 = (billingAddress.getAddressLine1() != null) ? billingAddress.getAddressLine1() : null;
				addressLine2 = (billingAddress.getAddressLine2() != null) ? billingAddress.getAddressLine2() : null;
				addressLine3 = (billingAddress.getAddressLine3() != null) ? billingAddress.getAddressLine3() : null;
			} else if (physicalAddress != null) {
				strName = (physicalAddress.getAddressLine1() != null) ? physicalAddress.getAddressLine1() : null;
				postCode = (physicalAddress.getAddressLine2() != null) ? physicalAddress.getAddressLine2() : null;
				townName = (physicalAddress.getAdminLevel1() != null) ? physicalAddress.getAdminLevel1() : null;
				country = (physicalAddress.getCountry() != null) ? physicalAddress.getCountry() : null;
				addressLine1 = (physicalAddress.getAddressLine1() != null) ? physicalAddress.getAddressLine1() : null;
				addressLine2 = (physicalAddress.getAddressLine2() != null) ? physicalAddress.getAddressLine2() : null;
				addressLine3 = (physicalAddress.getAddressLine3() != null) ? physicalAddress.getAddressLine3() : null;
			}
			
			PaymentInstructionInformation3 paymentInstruction = new PaymentInstructionInformation3();
			paymentInstruction.setPmtInfId(settlement.getId().toString());
			paymentInstruction.setPmtMtd(PaymentMethod3Code.TRF);
			paymentInstruction.setBtchBookg(true);
			paymentInstruction.setNbOfTxs(String.valueOf(settlement.getSettlementEntries().size()));
			paymentInstruction.setCtrlSum(settlement.getTotal());
			paymentInstruction.setReqdExctnDt(new SimpleDateFormat("yyyy-MM-dd").format(date));
			PartyIdentification32 debtor = new PartyIdentification32();
			debtor.setNm((domain.getBankingDetails() != null) ? domain.getBankingDetails().getAccountHolder() : "");
			debtor.setId(identification);
			paymentInstruction.setDbtr(debtor);
			CashAccount16 debtorAccount = new CashAccount16();
			AccountIdentification4Choice accountId = new AccountIdentification4Choice();
			accountId.setIBAN((domain.getBankingDetails() != null) ? domain.getBankingDetails().getAccountNumber() : "");
			GenericAccountIdentification1 accountIdentification = new GenericAccountIdentification1();
			accountIdentification.setId((domain.getBankingDetails() != null) ? (domain.getBankingDetails().getOrgId() != null) ? domain.getBankingDetails().getOrgId() : ""  : "");
			accountId.setOthr(accountIdentification);
			debtorAccount.setId(accountId);
			paymentInstruction.setDbtrAcct(debtorAccount);
			BranchAndFinancialInstitutionIdentification4 branchAndfinInstId = new BranchAndFinancialInstitutionIdentification4();
			FinancialInstitutionIdentification7 finInstId = new FinancialInstitutionIdentification7();
			finInstId.setBIC((domain.getBankingDetails() != null) ? domain.getBankingDetails().getBankIdentifierCode() : "");
			branchAndfinInstId.setFinInstnId(finInstId);
			paymentInstruction.setDbtrAgt(branchAndfinInstId);
			
			for (SettlementEntry entry: settlement.getSettlementEntries()) {
				CreditTransferTransactionInformation10 transInf = new CreditTransferTransactionInformation10();
				PaymentIdentification1 paymentId = new PaymentIdentification1();
				paymentId.setInstrId(entry.getId().toString());
				paymentId.setEndToEndId(entry.getId().toString());
				transInf.setPmtId(paymentId);
				AmountType3Choice amount = new AmountType3Choice();
				ActiveOrHistoricCurrencyAndAmount currencyAndAmt = new ActiveOrHistoricCurrencyAndAmount();
				currencyAndAmt.setCcy(domain.getCurrency());
				currencyAndAmt.setValue(entry.getAmount());
				amount.setInstdAmt(currencyAndAmt);
				EquivalentAmount2 eqvtAmount = new EquivalentAmount2();
				eqvtAmount.setCcyOfTrf(domain.getCurrency());
				eqvtAmount.setAmt(currencyAndAmt);
				amount.setEqvtAmt(eqvtAmount);
				transInf.setAmt(amount);
				PartyIdentification32 creditor = new PartyIdentification32();
				creditor.setNm(paymentTo);
				PostalAddress6 pstlAddr = new PostalAddress6();
				if (strName != null) pstlAddr.setStrtNm(strName);
				if (postCode != null) pstlAddr.setPstCd(postCode);
				if (townName != null) pstlAddr.setTwnNm(townName);
				if (country != null) pstlAddr.setCtry(country);
				if (addressLine1 != null) pstlAddr.getAdrLine().add(addressLine1);
				if (addressLine2 != null) pstlAddr.getAdrLine().add(addressLine2);
				if (addressLine3 != null) pstlAddr.getAdrLine().add(addressLine3);
				creditor.setPstlAdr(pstlAddr);
				transInf.setCdtr(creditor);
				CashAccount16 creditorAcct = new CashAccount16();
				AccountIdentification4Choice creditorAccountId = new AccountIdentification4Choice();
				GenericAccountIdentification1 creditorAcctIdentification = new GenericAccountIdentification1();
				creditorAcctIdentification.setId(settlement.getBankDetails().getAccountNumber());
				AccountSchemeName1Choice schemeName = new AccountSchemeName1Choice();
				schemeName.setCd("BBAN");
				creditorAcctIdentification.setSchmeNm(schemeName);
				creditorAccountId.setOthr(creditorAcctIdentification);
				creditorAcct.setId(creditorAccountId);
				transInf.setCdtrAcct(creditorAcct);
				RemittanceInformation5 remittanceInfm = new RemittanceInformation5();
				remittanceInfm.getUstrd().add(entry.getDescription());
				transInf.setRmtInf(remittanceInfm);
				
				paymentInstruction.getCdtTrfTxInf().add(transInf);
			}
			
			custCreditTransInit.getPmtInf().add(paymentInstruction);
		}
		
		Document document = new Document();
		document.setCstmrCdtTrfInitn(custCreditTransInit);
		
		return document;
	}
}

package lithium.service.access.provider.iovation.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.access.provider.iovation.data.AddEvidenceResponse;
import lithium.service.access.provider.iovation.data.CheckTransactionDetails;
import lithium.service.access.provider.iovation.data.CheckTransactionDetailsResponse;
import lithium.service.access.provider.iovation.data.RetractEvidenceResponse;
import lithium.service.access.provider.iovation.services.AddEvidenceService;
import lithium.service.access.provider.iovation.services.CheckTransactionDetailsService;
import lithium.service.access.provider.iovation.services.RetractEvidenceService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/{domainName}/iovation/")
@Slf4j
public class IovationController {
	@Autowired AddEvidenceService addEvidenceService;
	@Autowired CheckTransactionDetailsService checkTransactionDetailsService;
	@Autowired RetractEvidenceService retractEvidenceService;
	
	@PostMapping("/addevidence")
	public Response<AddEvidenceResponse> addEvidence(
		@PathVariable("domainName") String domainName,
		@RequestParam("evidenceType") String evidenceType,
		@RequestParam("comment") String comment,
		@RequestParam("type") String type,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("deviceAlias") String deviceAlias
	) {
		log.info("addEvidence (domainName: " + domainName + ", evidenceType: " + evidenceType + ", comment: " + comment +
				 ", type: " + type + ", accountCode: " + accountCode + ", deviceAlias: " + deviceAlias + ")");
		AddEvidenceResponse response = null;
		try {
			response = addEvidenceService.addEvidence(domainName, evidenceType, comment, type, accountCode, deviceAlias);
			return Response.<AddEvidenceResponse>builder().data(response).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<AddEvidenceResponse>builder().data(response).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/checktransactiondetails")
	public Response<CheckTransactionDetailsResponse> checkTransactionDetails(
		@PathVariable("domainName") String domainName,
		@RequestBody CheckTransactionDetails checkTransactionDetails
	) {
		log.debug("checkTransactionDetails (" + checkTransactionDetails.toString() + ")");
		CheckTransactionDetailsResponse response = null;
		try {
			response = checkTransactionDetailsService.checkTransactionDetails(domainName, checkTransactionDetails);
			log.debug("checkTransactionDetailsResponse ("+response+")");
			return Response.<CheckTransactionDetailsResponse>builder().data(response).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<CheckTransactionDetailsResponse>builder().data(response).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/retractevidence")
	public Response<RetractEvidenceResponse> retractEvidence(
		@PathVariable("domainName") String domainName,
		@RequestParam("accountCode") String accountCode,
		@RequestParam("deviceAlias") String deviceAlias,
		@RequestParam("evidenceType") String evidenceType
	) {
		log.info("retractEvidence (domainName: " + domainName + ", accountCode: " + accountCode + ", deviceAlias: " + deviceAlias +
				 ", evidenceType: " + evidenceType + ")");
		RetractEvidenceResponse response = null;
		try {
			response = retractEvidenceService.retractEvidence(domainName, accountCode, deviceAlias, evidenceType);
			return Response.<RetractEvidenceResponse>builder().data(response).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<RetractEvidenceResponse>builder().data(response).status(INTERNAL_SERVER_ERROR).build();
		}
	}
}

package lithium.service.access.provider.iovation.controllers;

import lithium.service.Response;
import lithium.service.access.provider.iovation.data.*;
import lithium.service.access.provider.iovation.services.AddEvidenceService;
import lithium.service.access.provider.iovation.services.CheckTransactionDetailsService;
import lithium.service.access.provider.iovation.services.RetractEvidenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.System;
import java.util.Base64;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@RestController
@RequestMapping("/fraud/v1/subs/{subscriberId}")
@Slf4j
public class MockApiController {
//	@Autowired AddEvidenceService addEvidenceService;
//	@Autowired CheckTransactionDetailsService checkTransactionDetailsService;
//	@Autowired RetractEvidenceService retractEvidenceService;
	
//	@PostMapping("/addevidence")
//	public Response<AddEvidenceResponse> addEvidence(
//		@PathVariable("domainName") String domainName,
//		@RequestParam("evidenceType") String evidenceType,
//		@RequestParam("comment") String comment,
//		@RequestParam("type") String type,
//		@RequestParam("accountCode") String accountCode,
//		@RequestParam("deviceAlias") String deviceAlias
//	) {
//		log.info("addEvidence (domainName: " + domainName + ", evidenceType: " + evidenceType + ", comment: " + comment +
//				 ", type: " + type + ", accountCode: " + accountCode + ", deviceAlias: " + deviceAlias + ")");
//		AddEvidenceResponse response = null;
//		try {
//			response = addEvidenceService.addEvidence(domainName, evidenceType, comment, type, accountCode, deviceAlias);
//			return Response.<AddEvidenceResponse>builder().data(response).status(OK).build();
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return Response.<AddEvidenceResponse>builder().data(response).status(INTERNAL_SERVER_ERROR).build();
//		}
//	}
	
	@PostMapping("/checks")
	public ResponseEntity<CheckTransactionDetailsResponse> checkTransactionDetails(
			@RequestHeader("Authorization") String authHeader,
			@PathVariable("subscriberId") String subscriberId,
			@RequestBody CheckTransactionDetails checkTransactionDetails
	) {
		//FIXME: Do some checking on the auth header, perhaps build in some rejection criteria for testing
		log.info("checkTransactionDetails (" + checkTransactionDetails.toString() + ")");
		CheckTransactionDetailsResponse response = null;
		try {
			boolean isAuthValid = isAuthValid(authHeader);
			response = CheckTransactionDetailsResponse.builder()
					.accountCode(checkTransactionDetails.getAccountCode())
					.details(Details.builder().build())
					.id(UUID.randomUUID())
					.reason("Because someone set it up this way")
					.result(fakeRandomResult())
					.trackingNumber(System.currentTimeMillis())
					.build();
			ResponseEntity<CheckTransactionDetailsResponse> respEnt = new ResponseEntity(response, HttpStatus.OK);
			return respEnt;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			ResponseEntity<CheckTransactionDetailsResponse> respEntErr = new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
			return respEntErr;
		}
	}

	private String fakeRandomResult() {
		Random r = new Random();
		int aValue = r.nextInt() % 3;
		switch (aValue) {
			case 0: return "A";
			case 1: return "D";
			case 2: return "R";
		}
		return "A";
	}

	private boolean isAuthValid(final String base64AuthHeaderString) {
		String decodedString = new String(Base64.getDecoder().decode(base64AuthHeaderString.replaceFirst("Basic ", "")));
		StringTokenizer st = new StringTokenizer(decodedString, ":");
		StringTokenizer st2 = new StringTokenizer(st.nextToken(), "/");

		String subscriberPasscode = st.nextToken();
		String subscriberId = st2.nextToken();
		String subscriberAccount = st2.nextToken();

		//For now returning true. Will build in some failure conditions later
		return true;
	}
	
//	@PostMapping("/retractevidence")
//	public Response<RetractEvidenceResponse> retractEvidence(
//		@PathVariable("domainName") String domainName,
//		@RequestParam("accountCode") String accountCode,
//		@RequestParam("deviceAlias") String deviceAlias,
//		@RequestParam("evidenceType") String evidenceType
//	) {
//		log.info("retractEvidence (domainName: " + domainName + ", accountCode: " + accountCode + ", deviceAlias: " + deviceAlias +
//				 ", evidenceType: " + evidenceType + ")");
//		RetractEvidenceResponse response = null;
//		try {
//			response = retractEvidenceService.retractEvidence(domainName, accountCode, deviceAlias, evidenceType);
//			return Response.<RetractEvidenceResponse>builder().data(response).status(OK).build();
//		} catch (Exception e) {
//			log.error(e.getMessage(), e);
//			return Response.<RetractEvidenceResponse>builder().data(response).status(INTERNAL_SERVER_ERROR).build();
//		}
//	}
}

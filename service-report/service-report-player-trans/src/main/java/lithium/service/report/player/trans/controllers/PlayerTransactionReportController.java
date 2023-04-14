package lithium.service.report.player.trans.controllers;

import lithium.service.Response;
import lithium.service.Response.Status;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.report.player.trans.data.entities.PlayerTransaction;
import lithium.service.report.player.trans.data.entities.PlayerTransactionRequest;
import lithium.service.report.player.trans.services.PlayerTransactionReportExcelService;
import lithium.service.report.player.trans.services.PlayerTransactionReportService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@Slf4j
@RestController
public class PlayerTransactionReportController {
	
	@Autowired PlayerTransactionReportService playerTranReportService;
	@Autowired PlayerTransactionReportExcelService playerTransactionReportExcelService; 

	@RequestMapping("/findByDateRangeAndUserGuid")
	public DataTableResponse<PlayerTransaction> findByDateRangeAndUserGuid(
			@RequestParam("startDate") String startDate, 
			@RequestParam("endDate") String endDate, 
			@RequestParam("userGuid") String userGuid, 
			DataTableRequest request,
			Principal principal) {
		log.info("Data request for player trans: " + request +" start: " + startDate + " end: " + endDate + " userGuid: " + userGuid);
		DateTime sDate = new DateTime(startDate);
		DateTime eDate = new DateTime(endDate);
		
		PlayerTransactionRequest ptr = playerTranReportService.registerPlayerTransactionRequest(sDate, eDate, userGuid, principal);
		
		if (playerTranReportService.isPlayerTranDataReady(sDate, eDate, userGuid)) {
			log.debug("Data generation completed before. Can send data now");
			return playerTranReportService.getPlayerTransactions(request, sDate, eDate, userGuid, ptr);
		} else if (playerTranReportService.isPlayerTranDataProcessing(sDate, eDate, userGuid)) {
			log.debug("Data still being generated. Data will not be sent");
			//TODO: handle case where data is still being generated
		} //else {
//			log.debug("Going to start data generation. Nothing generated before or data was purged");
//			playerTranReportService.generateTransactionData(sDate, eDate, userGuid, principal, ptr);
//			return playerTranReportService.getPlayerTransactions(request, sDate, eDate, userGuid, ptr);
//		}

		return null;
	}
	
	@RequestMapping("/generateByDateRangeAndUserGuid")
	public Response<Boolean> generateByDateRangeAndUserGuid(
			@RequestParam("startDate") String startDate, 
			@RequestParam("endDate") String endDate, 
			@RequestParam("userGuid") String userGuid,
			Principal principal) {
		log.info("Data generation request for player trans: start: " + startDate + " end: " + endDate + " userGuid: " + userGuid);
		DateTime sDate = new DateTime(startDate);
		DateTime eDate = new DateTime(endDate);
		
		PlayerTransactionRequest ptr = playerTranReportService.registerPlayerTransactionRequest(sDate, eDate, userGuid, principal);
		
		if (playerTranReportService.isPlayerTranDataReady(sDate, eDate, userGuid)) {
			log.debug("Data generation completed before. Can send data now");
			return Response.<Boolean>builder().data(true).status(Status.OK).build();
		} else if (playerTranReportService.isPlayerTranDataProcessing(sDate, eDate, userGuid)) {
			log.debug("Data still being generated.");
			return Response.<Boolean>builder().data(false).status(Status.OK).build();
		} else {
			log.debug("Going to start data generation. Nothing generated before or data was purged");
			playerTranReportService.generateTransactionData(sDate, eDate, userGuid, principal, ptr);
			return Response.<Boolean>builder().data(false).status(Status.OK).build();
		}

	}

	@ResponseBody
	@RequestMapping("/xls")
	public void xls(
		@RequestParam("startDate") String startDate,
		@RequestParam("endDate") String endDate,
		@RequestParam("userGuid") String userGuid,
		HttpServletResponse response
	) throws Exception {
		DateTime sDate = new DateTime(startDate);
		DateTime eDate = new DateTime(endDate);
		String fileName = userGuid+"_"+sDate.toString("ddMMMyyyy")+"_"+eDate.toString("ddMMMyyyy") + ".xlsx";
		String mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName +"\""));
		response.setHeader("x-filename", String.format(fileName));
		playerTransactionReportExcelService.xls(userGuid, sDate, eDate, response.getOutputStream());
	}
}

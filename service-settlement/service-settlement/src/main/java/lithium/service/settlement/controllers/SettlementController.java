package lithium.service.settlement.controllers;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.settlement.data.entities.Settlement;
import lithium.service.settlement.data.entities.SettlementEntry;
import lithium.service.settlement.services.SettlementPDFService;
import lithium.service.settlement.services.SettlementService;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/settlement/{id}")
@Slf4j
public class SettlementController {
	@Autowired SettlementService settlementService;
	@Autowired SettlementPDFService settlementPdfService;
	
	@GetMapping
	public Response<Settlement> get(
		@PathVariable("id") Settlement settlement,
		LithiumTokenUtil tokenUtil
	) {
		try {
			settlement = settlementService.enrichDataWithExternalUserOrEntity(settlement);
			return Response.<Settlement>builder().data(settlement).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Settlement>builder().data(settlement).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/table")
	public DataTableResponse<SettlementEntry> table(
		@PathVariable("id") Settlement settlement,
		LithiumTokenUtil tokenUtil,
		DataTableRequest request
	) {
		return settlementService.settlementEntriesTable(settlement, request);
	}
	
	@PostMapping("/entry/add")
	public Response<Settlement> addSettlementEntry(
		@PathVariable("id") Settlement settlement,
		@RequestBody lithium.service.settlement.client.objects.SettlementEntry entry,
		LithiumTokenUtil tokenUtil
	) {
		Settlement s = null;
		try {
			s = settlementService.addSettlementEntry(settlement, entry);
			return Response.<Settlement>builder().data(s).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Settlement>builder().data(s).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/finalize")
	public Response<Settlement> finalizeSettlement(
		@PathVariable("id") Settlement settlement,
		LithiumTokenUtil tokenUtil
	) throws LithiumServiceClientFactoryException, IOException {
		Settlement s = null;
		try {
			s = settlementService.finalizeSettlement(settlement);
			return Response.<Settlement>builder().data(s).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Settlement>builder().data(s).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/pdf/preview")
	public @ResponseBody void previewPdf(
		@PathVariable("id") Settlement settlement,
		LithiumTokenUtil tokenUtil,
		HttpServletResponse response
	) throws Exception {
		String fileName = "settlement.pdf";
		String mimeType = "application/pdf";
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName +"\""));
		OutputStream os = response.getOutputStream();
		os.write(settlementPdfService.preview(settlement));
		response.flushBuffer();
	}
	
	@GetMapping("/pdf/download")
	public @ResponseBody void downloadPdf(
		@PathVariable("id") Settlement settlement,
		LithiumTokenUtil tokenUtil,
		HttpServletResponse response
	) throws IOException {
		String fileName = "settlement.pdf";
		String mimeType = "application/pdf";
		response.setContentType(mimeType);
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName +"\""));
		OutputStream os = response.getOutputStream();
		os.write(settlement.getPdf().getPdf());
		response.flushBuffer();
	}
	
	@PostMapping("/pdf/resend")
	public Response<Settlement> resendPdf(
		@PathVariable("id") Settlement settlement,
		LithiumTokenUtil tokenUtil
	) {
		Settlement s = null;
		try {
			s = settlementService.resendPdf(settlement);
			return Response.<Settlement>builder().data(s).status(OK).build();
		} catch (Exception e) {
			return Response.<Settlement>builder().data(s).status(INTERNAL_SERVER_ERROR).build();
		}
	}
}

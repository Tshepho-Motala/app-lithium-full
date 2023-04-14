package lithium.service.settlement.controllers;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;
import lithium.service.Response;
import lithium.service.client.LithiumServiceClientFactoryException;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.settlement.credtransfinitv3.xml.Document;
import lithium.service.settlement.data.entities.BatchSettlements;
import lithium.service.settlement.services.BatchSettlementsService;
import lithium.service.settlement.utils.PlayerDomainUtil;
import lithium.tokens.LithiumTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import java.io.IOException;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.OK;

@RestController
@RequestMapping("/batch/settlements")
@Slf4j
public class BatchSettlementsController {
	@Autowired BatchSettlementsService service;
	@Autowired PlayerDomainUtil playerDomainUtil;
	
	@GetMapping("/table")
	public DataTableResponse<BatchSettlements> table(
		DataTableRequest request,
		LithiumTokenUtil tokenUtil
	) {
		return service.table(request, playerDomainUtil.getDomains(tokenUtil, "SETTLEMENTS_MANAGE"));
	}
	
	@GetMapping("/{batchName}/isunique")
	public Response<Boolean> batchNameIsUnique(
		@PathVariable("batchName") String batchName,
		LithiumTokenUtil tokenUtil
	) {
		Boolean result = null;
		try {
			result = service.batchNameIsUnique(tokenUtil.playerDomainWithRole("SETTLEMENTS_MANAGE").getName(), batchName);
			return Response.<Boolean>builder().data(result).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<Boolean>builder().data(result).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/{domainName}/{batchName}/initBatchRerun")
	public Response<BatchSettlements> initBatchRerun(
		@PathVariable("domainName") String domainName,
		@PathVariable("batchName") String batchName,
		LithiumTokenUtil tokenUtil
	) {
		BatchSettlements bs = null;
		try {
			bs = service.initBatchRerun(domainName, batchName);
			return Response.<BatchSettlements>builder().data(bs).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<BatchSettlements>builder().data(bs).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/{domainName}/{batchName}/closeBatchRerun")
	public Response<BatchSettlements> closeBatchRerun(
		@PathVariable("domainName") String domainName,
		@PathVariable("batchName") String batchName,
		LithiumTokenUtil tokenUtil
	) {
		BatchSettlements bs = null;
		try {
			bs = service.closeBatchRerun(domainName, batchName);
			return Response.<BatchSettlements>builder().data(bs).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<BatchSettlements>builder().data(bs).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/{id}")
	public Response<BatchSettlements> get(
		@PathVariable("id") BatchSettlements batchSettlements,
		LithiumTokenUtil tokenUtil
	) {
		return Response.<BatchSettlements>builder().data(batchSettlements).status(OK).build();
	}
	
	@PostMapping("/{id}/finalize")
	public Response<BatchSettlements> finalizeBatchSettlements(
		@PathVariable("id") BatchSettlements batchSettlements,
		LithiumTokenUtil tokenUtil
	) {
		BatchSettlements bs = null;
		try {
			bs = service.finalizeBatchSettlements(batchSettlements);
			return Response.<BatchSettlements>builder().data(bs).status(OK).build();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return Response.<BatchSettlements>builder().data(bs).status(INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@GetMapping("/{id}/export/xls")
	public @ResponseBody void exportToXls(
		@PathVariable("id") BatchSettlements batchSettlements,
		LithiumTokenUtil tokenUtil,
		HttpServletResponse response
	) throws IOException {
		String fileName = "batch-settlements-" + batchSettlements.getId() + ".xlsx";
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", String.format("attachment; filename=\"" + fileName +"\""));
		service.exportToXls(batchSettlements, response.getOutputStream());
		response.flushBuffer();
	}
	
	@GetMapping(value="/{id}/export/nets")
	public @ResponseBody void exportToNets(
		@PathVariable("id") BatchSettlements batchSettlements,
		LithiumTokenUtil tokenUtil,
		HttpServletResponse response
	) throws DatatypeConfigurationException, LithiumServiceClientFactoryException, JsonGenerationException, JsonMappingException, IOException {
		response.setContentType("application/xml");
		Document document = service.exportToNets(batchSettlements);
		JacksonXmlModule module = new JacksonXmlModule();
		ObjectMapper mapper = new XmlMapper(module);
		AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		mapper.setAnnotationIntrospector(jaxbIntrospector);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.writeValue(response.getOutputStream(), document);
	}
}

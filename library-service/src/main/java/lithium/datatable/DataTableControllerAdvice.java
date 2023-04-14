package lithium.datatable;

import lithium.service.client.datatable.DataTablePostRequest;
import lithium.service.client.datatable.DataTableRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class DataTableControllerAdvice {

	@ModelAttribute
	public DataTableRequest dataTableRequest(WebRequest request) {
		
		if (request == null) return null;
		if (request.getParameter("draw") == null || request.getParameter("start") == null || request.getParameter("length") == null) return null;

		log.debug("Creating dataTable request");

		String echo = request.getParameter("draw");
		String search = request.getParameter("search[value]");
		
		Integer start = new Integer(request.getParameter("start"));
		Integer length = new Integer(request.getParameter("length"));
		
		PageRequest pageRequest = PageRequest.of(start/length, length);

		String sortColumnIndex = request.getParameter("order[0][column]");
		if (sortColumnIndex != null) {
			String sortColumnName = request.getParameter("columns[" + sortColumnIndex + "][data]");
			if (sortColumnName == null || sortColumnName.isEmpty()) {
				log.warn("Invalid column index in sort. No column name found in request. Is the column a data column?: " + request + " sortColumnIndex " + sortColumnIndex + " " + request.getDescription(true));
			} else {
				String sortDir = request.getParameter("order[0][dir]"); 
				Sort sort = Sort.by(sortDir.equals("asc")? Direction.ASC:Direction.DESC, sortColumnName);
				pageRequest = PageRequest.of(start/length, length, sort);
			}
		}

		return new DataTableRequest(pageRequest, echo, search);
	}

	@ModelAttribute
	public DataTablePostRequest dataTablePostRequest(WebRequest request) {
		if (request == null) return null;
		if (request.getParameter("draw") == null || request.getParameter("start") == null || request.getParameter("length") == null) return null;

		log.debug("Creating dataTable POST request");

		String echo = request.getParameter("draw");
		String search = request.getParameter("search[value]");

		Integer start = new Integer(request.getParameter("start"));
		Integer length = new Integer(request.getParameter("length"));

		PageRequest pageRequest = PageRequest.of(start/length, length);

		String sortColumnIndex = request.getParameter("order[0][column]");
		if (sortColumnIndex != null) {
			String sortColumnName = request.getParameter("columns[" + sortColumnIndex + "][data]");
			if (sortColumnName == null || sortColumnName.isEmpty()) {
				log.warn("Invalid column index in sort. No column name found in request. Is the column a data column?: " + request + " sortColumnIndex " + sortColumnIndex + " " + request.getDescription(true));
			} else {
				String sortDir = request.getParameter("order[0][dir]");
				Sort sort = Sort.by(sortDir.equals("asc")? Direction.ASC:Direction.DESC, sortColumnName);
				pageRequest = PageRequest.of(start/length, length, sort);
			}
		}

		Map<String, String[]> requestData = request.getParameterMap()
		.entrySet()
		.stream()
		.filter(map -> map.getKey().startsWith("requestData"))
		.collect(Collectors.toMap(map -> map.getKey().replaceAll("requestData\\[", "").replaceAll("]", ""), map -> {
			String[] values = request.getParameterValues(map.getKey());
			return values;
		}));

		DataTablePostRequest dtpr = new DataTablePostRequest();
		dtpr.setEcho(echo);
		dtpr.setPageRequest(pageRequest);
		dtpr.setSearchValue(search);
		dtpr.setRequestData(requestData);

		return dtpr;
	}
}

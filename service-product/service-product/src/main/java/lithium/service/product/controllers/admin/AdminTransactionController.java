package lithium.service.product.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.product.data.entities.Transaction;
import lithium.service.product.services.ProductService;
import lithium.service.product.services.TransactionService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/product/admin/transaction")
public class AdminTransactionController {
	@Autowired ProductService productService;
	@Autowired TransactionService transactionService;
	
	@GetMapping("/table")
	public DataTableResponse<Transaction> productTable(@RequestParam("domains") List<String> domains, DataTableRequest request) throws Exception {
		log.trace("transactionTable");
		domains.removeIf(d -> d.isEmpty());
		Page<Transaction> table = transactionService.findByDomains(domains, request.getSearchValue(), request.getPageRequest());
		return new DataTableResponse<>(request, table);
	}
}
package lithium.service.accounting.provider.internal.controllers;

import lithium.service.accounting.provider.internal.data.entities.AccountCode;
import lithium.service.accounting.provider.internal.services.AccountCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accountcode")
public class AccountCodeController {
	@Autowired AccountCodeService accountCodeService;
	
	@RequestMapping("/findorcreate")
	@Retryable
	public ResponseEntity<AccountCode> findOrCreateAccountCode(String code) {
		return new ResponseEntity<>(accountCodeService.findOrCreate(code), HttpStatus.OK);
	}
}

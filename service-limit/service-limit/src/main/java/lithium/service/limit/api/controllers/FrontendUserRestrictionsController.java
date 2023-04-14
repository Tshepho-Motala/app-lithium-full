package lithium.service.limit.api.controllers;

import lithium.exceptions.Status500InternalServerErrorException;
import lithium.service.domain.client.util.LocaleContextProcessor;
import lithium.service.limit.client.objects.Access;
import lithium.service.limit.services.UserRestrictionService;
import lithium.service.translate.client.objects.RestrictionError;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/user-restrictions/v1")
public class FrontendUserRestrictionsController {
	@Autowired private UserRestrictionService service;
	@Autowired private MessageSource messageSource;
	@Autowired private LocaleContextProcessor localeContextProcessor;

	@GetMapping("/checkAccess")
	public Access checkAccess(LithiumTokenUtil tokenUtil, String locale) throws Status500InternalServerErrorException {
		try {
			localeContextProcessor.setLocaleContextHolder(locale, tokenUtil.domainName());
			Access access = service.checkAccess(tokenUtil.guid());
			if (!StringUtil.isEmpty(access.getCasinoErrorMessage())) access.setCasinoErrorMessage(RestrictionError.CASINO.getResponseMessageLocal(messageSource, tokenUtil.domainName(), access.getCasinoErrorMessage()));
			if (!StringUtil.isEmpty(access.getLoginErrorMessage())) access.setLoginErrorMessage(RestrictionError.LOGIN.getResponseMessageLocal(messageSource, tokenUtil.domainName(), access.getLoginErrorMessage()));
			if (!StringUtil.isEmpty(access.getDepositErrorMessage())) access.setDepositErrorMessage(RestrictionError.DEPOSIT.getResponseMessageLocal(messageSource, tokenUtil.domainName(), access.getDepositErrorMessage()));
			if (!StringUtil.isEmpty(access.getWithdrawErrorMessage())) access.setWithdrawErrorMessage(RestrictionError.WITHDRAW.getResponseMessageLocal(messageSource, tokenUtil.domainName(), access.getWithdrawErrorMessage()));
			if (!StringUtil.isEmpty(access.getBetPlacementErrorMessage())) access.setBetPlacementErrorMessage(RestrictionError.BET_PLACEMENT.getResponseMessageLocal(messageSource, tokenUtil.domainName(), access.getBetPlacementErrorMessage()));
			return access;
		} catch (Status500InternalServerErrorException e) {
			throw new Status500InternalServerErrorException(messageSource.getMessage("ERROR_DICTIONARY.LOGIN.INTERNAL_SERVER_ERROR", new Object[]{new lithium.service.translate.client.objects.Domain(tokenUtil.getJwtUser().getDomainName())}, "Internal server error.", LocaleContextHolder.getLocale()), e.getStackTrace());
		}
	}

}

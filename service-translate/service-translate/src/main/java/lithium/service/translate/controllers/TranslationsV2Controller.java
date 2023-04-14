package lithium.service.translate.controllers;

import lithium.client.changelog.ChangeLogService;
import lithium.client.changelog.objects.ChangeLogRequest;
import lithium.client.changelog.objects.ChangeLogs;
import lithium.service.Response;
import lithium.service.client.datatable.DataTableRequest;
import lithium.service.client.datatable.DataTableResponse;
import lithium.service.client.page.SimplePageImpl;
import lithium.service.domain.client.exceptions.Status550ServiceDomainClientException;
import lithium.service.translate.client.objects.Module;
import lithium.service.translate.client.objects.SubModule;
import lithium.service.translate.data.entities.Domain;
import lithium.service.translate.data.entities.TranslationKeyV2;
import lithium.service.translate.data.objects.ErrorMessageResponse;
import lithium.service.translate.data.objects.TranslationKeyRequest;
import lithium.service.translate.data.objects.TranslationV2;
import lithium.service.translate.exceptions.Status400BadRequestException;
import lithium.service.translate.exceptions.Status409DuplicateMessageCodeException;
import lithium.service.translate.exceptions.Status422InvalidLanguageException;
import lithium.service.translate.services.TranslationService;
import lithium.tokens.LithiumTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/apiv2")
public class TranslationsV2Controller {

    @Autowired TranslationService translationService;
    @Autowired ChangeLogService changeLogService;

    @GetMapping("/translations/{domainName}/{subModule}/list")
    public DataTableResponse<ErrorMessageResponse> getTranslationsList(@PathVariable("domainName") String domainName,
                                                                       @PathVariable(name = "subModule") String subModule,
                                                                       @RequestParam(name = "domainSpecific") Boolean domainSpecific,
                                                                       DataTableRequest request) {
        if(request == null) {
            PageRequest pageRequest = PageRequest.of(0, 25, Sort.Direction.DESC, "id");
            request = new DataTableRequest();
            request.setPageRequest(pageRequest);
        }

        request.setPageRequest(PageRequest.of(request.getPageRequest().getPageNumber(),
                request.getPageRequest().getPageSize() > 100 ? 100 : request.getPageRequest().getPageSize(),
                Sort.Direction.DESC, "id"));

        Page<ErrorMessageResponse> errorTranslations = new SimplePageImpl<>(new ArrayList<>(), 0, 1, 0);;

        SubModule sm = SubModule.fromName(subModule.toUpperCase());
        if (sm != null) {
            errorTranslations = translationService.getErrorTranslations(request, domainName, Module.ERROR_DICTIONARY, sm, domainSpecific);
        }
        return new DataTableResponse<>(request, errorTranslations);
    }

    @GetMapping("/translations/{domainName}/get/{id}")
    public List<TranslationV2> getTranslationsById(@PathVariable("domainName") String domainName,
                                                   @PathVariable("id") Long id) {
        return translationService.getCodeValues(id, domainName);
    }

    @GetMapping("/translations/{domainName}/get/key")
    public Response<Long> getKeyIdByCode(@PathVariable("domainName") String domainName, @RequestParam("code") String code) {
        Optional<TranslationKeyV2> byCode = translationService.findKeyByCode(code);
        if (byCode.isPresent()) {
            return Response.<Long>builder()
                    .data(byCode.get().getId())
                    .status(Response.Status.OK)
                    .build();
        }
        return Response.<Long>builder().status(Response.Status.NOT_FOUND).build();
    }

    @PostMapping("/translations/{domainName}/add")
    public Response<TranslationV2> addTranslationValue(@RequestBody TranslationV2 translationV2, LithiumTokenUtil tokenUtil) {
        TranslationV2 translation = translationService.addTranslation(translationV2.getDomainName(), translationV2.getLanguage(), translationV2.getKeyId(), translationV2.getValue(), tokenUtil);
        return Response.<TranslationV2>builder().data(translation).status(Response.Status.OK).build();
    }

    @PostMapping("/translations/{domainName}/{subModule}/key/create")
    public Response<TranslationKeyV2> addTranslationKeyAndValue(@PathVariable("domainName") String domainName,
                                                                @PathVariable("subModule") String subModule,
                                                                @RequestBody TranslationKeyRequest translationKeyRequest, LithiumTokenUtil tokenUtil)
            throws Status409DuplicateMessageCodeException, Status422InvalidLanguageException, Status550ServiceDomainClientException, Status400BadRequestException {
        translationKeyRequest.setDomainName(domainName);
        translationKeyRequest.setMessageType(subModule);
        translationKeyRequest.setMessageLanguage("en"); //default to en until languages are supported on this
        TranslationKeyV2 translationKeyV2 = translationService.createKeyV2andDefaultTranslation(translationKeyRequest, tokenUtil);
        return Response.<TranslationKeyV2>builder().data(translationKeyV2).status(Response.Status.OK).build();
    }

    @PostMapping("/translations/{domainName}/edit")
    public void editTranslationValue(@RequestBody TranslationV2 translationV2, LithiumTokenUtil tokenUtil) {
        translationService.editTranslation(translationV2.getValueId(), translationV2.getValue(), tokenUtil);
    }

    @PostMapping("/translations/{domainName}/delete")
    public void deleteTranslationValue(@RequestBody TranslationV2 translationV2, LithiumTokenUtil tokenUtil) {
        translationService.deleteTranslation(translationV2.getValueId(), tokenUtil);
    }

    @DeleteMapping("/translations/{domainName}/remove/user-defined")
    public Response<String> deleteTranslation(@PathVariable("domainName") String domainName, @RequestParam("key") Long key, LithiumTokenUtil tokenUtil) {
        if (translationService.deleteUserDefinedByKeyId(key, domainName, tokenUtil)) {
            return Response.<String>builder().data(Response.Status.OK.name()).status(Response.Status.OK).build();
        } else {
            return Response.<String>builder().data(Response.Status.NOT_FOUND.name()).status(Response.Status.NOT_FOUND).build();
        }
    }

    @GetMapping(value = "/translations/{domainName}/changelogs")
    public @ResponseBody Response<ChangeLogs> changeLogs(@PathVariable("domainName") String domainName, @RequestParam int p) throws Exception {
        Domain domain = translationService.findDomain(domainName);
        Long domainId = domain != null ? domain.getId() : -1;
        return changeLogService.listLimited(ChangeLogRequest.builder()
                .entityRecordId(domainId)
                .entities(new String[] {
                        "domain.errormessage.registration",
                        "domain.errormessage.login",
                        "domain.errormessage.password",
                        "domain.errormessage.cashier",
                        "domain.errormessage.myaccount",
                        "domain.errormessage.limitsystemaccess",
                        "domain.errormessage.systemrestriction"
                })
                .page(p)
                .build()
        );
    }

    @DeleteMapping("/translations/delete")
    public Response<String> deleteTranslationByCode(@RequestParam("code") String code) {
        if (translationService.deleteByCode(code)) {
            return Response.<String>builder().data(Response.Status.OK.name()).status(Response.Status.OK).build();
        } else {
            return Response.<String>builder().data(Response.Status.NOT_FOUND.name()).status(Response.Status.NOT_FOUND).build();
        }
    }

}

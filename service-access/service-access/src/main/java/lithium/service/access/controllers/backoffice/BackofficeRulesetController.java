package lithium.service.access.controllers.backoffice;

import static lithium.service.Response.Status.INTERNAL_SERVER_ERROR;
import static lithium.service.Response.Status.NOT_FOUND;
import static lithium.service.Response.Status.OK;

import lithium.client.changelog.objects.ChangeLogs;
import lithium.exceptions.Status400BadRequestException;
import lithium.service.Response;
import lithium.service.access.client.objects.Action;
import lithium.service.access.data.entities.AccessRule;
import lithium.service.access.data.objects.RuleEdit;
import lithium.service.access.data.objects.RuleMessageUpdate;
import lithium.service.access.services.RulesetService;
import lithium.tokens.LithiumTokenUtil;
import lithium.util.DomainValidationUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/backoffice/ruleset/{rulesetId}")
public class BackofficeRulesetController {

  @Autowired
  RulesetService rulesetService;
  @Autowired
  MessageSource messageSource;

  @GetMapping("/info")
  public Response<AccessRule> info(
      @PathVariable("rulesetId") AccessRule ruleset,
      LithiumTokenUtil tokenUtil
  ) {
    try {
      if (ruleset == null) {
        return Response.<AccessRule>builder().status(NOT_FOUND).build();
      }
      log.debug("accessRule: " + ruleset);
      DomainValidationUtil.validate(ruleset.getDomain().getName(), tokenUtil, "ACCESSRULES_VIEW", "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      return Response.<AccessRule>builder().data(ruleset).status(OK).build();
    } catch (Exception e) {
      return Response.<AccessRule>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @PostMapping("/info")
  public Response<AccessRule> save(
      @PathVariable("rulesetId") AccessRule ruleset,
      @RequestParam("name") String name,
      @RequestParam(required = false, name = "description") String description,
      @RequestParam("enabled") boolean enabled,
      LithiumTokenUtil tokenUtil
  ) {
    try {
      DomainValidationUtil.validate(ruleset.getDomain().getName(), tokenUtil, "ACCESSRULES_EDIT", "ACCESSRULES_ADD");

      if (!ruleset.getName().equals(name)) {
        throw new Status400BadRequestException("Name can not be updated.");
      }

      ruleset = rulesetService.save(ruleset, name, description, enabled, tokenUtil.guid());
      return Response.<AccessRule>builder().data(ruleset).status(OK).build();
    } catch (Exception e) {
      return Response.<AccessRule>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @PostMapping("/default-action")
  public Response<AccessRule> rulesetDefaultAction(
      @PathVariable("rulesetId") AccessRule ruleset,
      @RequestParam("action") Action defaultAction,
      LithiumTokenUtil tokenUtil
  ) {
    try {
      DomainValidationUtil.validate(ruleset.getDomain().getName(), tokenUtil, "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      ruleset = rulesetService.save(ruleset, defaultAction, tokenUtil.guid());
      return Response.<AccessRule>builder().data(ruleset).status(OK).build();
    } catch (Exception e) {
      return Response.<AccessRule>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }


  @GetMapping("/eligable-lists")
  public Response<Iterable<lithium.service.access.data.entities.List>> eligableLists(
      @PathVariable("rulesetId") AccessRule accessRule,
      LithiumTokenUtil tokenUtil
  ) {
    try {
      DomainValidationUtil.validate(accessRule.getDomain().getName(), tokenUtil, "ACCESSRULES_VIEW", "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      return Response.<Iterable<lithium.service.access.data.entities.List>>builder().data(rulesetService.eligableLists(accessRule)).status(OK).build();
    } catch (Exception e) {
      return Response.<Iterable<lithium.service.access.data.entities.List>>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @PostMapping("/rule-add")
  public Response<AccessRule> ruleAdd(
      @PathVariable("rulesetId") AccessRule ruleset,
      @RequestBody RuleEdit ruleAdd,
      LithiumTokenUtil tokenUtil
  ) {
    try {
      DomainValidationUtil.validate(ruleset.getDomain().getName(), tokenUtil, "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      ruleAdd.setRuleset(ruleset);
      log.info("Add Rule : " + ruleAdd + "");
      AccessRule accessRule = rulesetService.addRule(ruleAdd);
      return Response.<AccessRule>builder().data(accessRule).status(OK).build();
    } catch (Exception e) {
      return Response.<AccessRule>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @PostMapping("/rule/{ruleId}/orderup")
  public Response<AccessRule> changeRuleOrderUp(
      @PathVariable("rulesetId") AccessRule ruleset,
      @PathVariable("ruleId") Long ruleId,
      @RequestParam("type") String type,
      LithiumTokenUtil tokenUtil
  ) {
    try {
      DomainValidationUtil.validate(ruleset.getDomain().getName(), tokenUtil, "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      log.info("changeRuleOrderUp " + ruleset + " rule: " + ruleId + " type: " + type);
      AccessRule accessRule = rulesetService.increasePriority(ruleset, ruleId, type, tokenUtil.guid());
      return Response.<AccessRule>builder().data(accessRule).status(OK).build();
    } catch (Exception e) {
      return Response.<AccessRule>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @PostMapping("/rule/{ruleId}/orderdown")
  public Response<AccessRule> changeRuleOrderDown(
      @PathVariable("rulesetId") AccessRule ruleset,
      @PathVariable("ruleId") Long ruleId,
      @RequestParam("type") String type,
      LithiumTokenUtil tokenUtil
  ) {
    try {
      DomainValidationUtil.validate(ruleset.getDomain().getName(), tokenUtil, "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      log.info("changeRuleOrderDown " + ruleset + " rule: " + ruleId + " type: " + type);
      AccessRule accessRule = rulesetService.decreasePriority(ruleset, ruleId, type, tokenUtil.guid());
      return Response.<AccessRule>builder().data(accessRule).status(OK).build();
    } catch (Exception e) {
      return Response.<AccessRule>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @PostMapping("/rule/{ruleId}/update-messages")
  public Response<AccessRule> changeRuleMessage(
      @PathVariable("rulesetId") AccessRule ruleset,
      @PathVariable("ruleId") Long ruleId,
      @RequestBody RuleMessageUpdate ruleMessageUpdate,
      LithiumTokenUtil tokenUtil
  ) {
    try {
      DomainValidationUtil.validate(ruleset.getDomain().getName(), tokenUtil, "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      log.info("changeRuleMessage " + ruleset + " rule: " + ruleId + " type: " + ruleMessageUpdate.getType() + " msg: " + ruleMessageUpdate);
      AccessRule accessRule = rulesetService.changeRuleMessage(ruleset, ruleId, ruleMessageUpdate.getType(), ruleMessageUpdate, tokenUtil.guid());
      return Response.<AccessRule>builder().data(accessRule).status(OK).build();
    } catch (Exception e) {
      return Response.<AccessRule>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @PostMapping("/rule/{ruleId}/edit")
  public Response<AccessRule> ruleEditSave(
      @PathVariable("rulesetId") AccessRule ruleset,
      @PathVariable("ruleId") Long ruleId,
      @RequestBody RuleEdit ruleEdit,
      LithiumTokenUtil tokenUtil
  ) {
    try {
      DomainValidationUtil.validate(ruleset.getDomain().getName(), tokenUtil, "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      ruleEdit.setRuleId(ruleId);
      ruleEdit.setRuleset(ruleset);
      log.info("Edit Rule : " + ruleId + " ruleEdit: " + ruleEdit + "");
      AccessRule accessRule = rulesetService.editRule(ruleEdit, tokenUtil.guid());
      return Response.<AccessRule>builder().data(accessRule).status(OK).build();
    } catch (Exception e) {
      return Response.<AccessRule>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @PostMapping("/rule/{ruleId}/delete")
  public Response<AccessRule> ruleDelete(
      @PathVariable("rulesetId") AccessRule ruleset,
      @PathVariable("ruleId") Long ruleId,
      @RequestParam("type") String type,
      LithiumTokenUtil tokenUtil
  ) {
    try {
      DomainValidationUtil.validate(ruleset.getDomain().getName(), tokenUtil, "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      log.info("Delete Rule (" + type + ") : " + ruleId + " ruleset: " + ruleset + "");
      AccessRule accessRule = rulesetService.deleteRule(ruleset, ruleId, type, tokenUtil.guid());
      return Response.<AccessRule>builder().data(accessRule).status(OK).build();
    } catch (Exception e) {
      return Response.<AccessRule>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @GetMapping("/rule/{ruleId}/has-tran-data")
  public Response<Boolean> ruleTranData(
      @PathVariable("rulesetId") AccessRule ruleset,
      @PathVariable("ruleId") Long ruleId,
      @RequestParam("type") String type,
      LithiumTokenUtil tokenUtil
  ) {
    try {
      DomainValidationUtil.validate(ruleset.getDomain().getName(), tokenUtil, "ACCESSRULES_VIEW", "ACCESSRULES_EDIT", "ACCESSRULES_ADD");
      return Response.<Boolean>builder().data(rulesetService.ruleTranData(ruleset, ruleId, type)).status(OK).build();
    } catch (Exception e) {
      return Response.<Boolean>builder().status(INTERNAL_SERVER_ERROR).message(e.getMessage()).build();
    }
  }

  @GetMapping("/changelogs")
  public @ResponseBody
  Response<ChangeLogs> changeLogs(
      @PathVariable Long rulesetId,
      @RequestParam int p
  ) throws Exception {
    return rulesetService.changeLogs(rulesetId, p);
  }
}






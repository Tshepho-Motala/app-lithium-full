'use strict';

angular.module('lithium-rest-accessrule', ['restangular']).factory('accessRulesRest', ['$log', 'Restangular',
	function($log, Restangular) {
		try {
			var service = {};
			var accessRulesService = Restangular.withConfig(function(RestangularConfigurer) {
				RestangularConfigurer.setBaseUrl('services/service-access');
			});

			service.ruleset = function(rulesetId) {
				return accessRulesService.one('backoffice').one('ruleset', rulesetId).one('info').get();
			}
			service.saveRuleset = function(rulesetId, name, description, enabled) {
				return accessRulesService.one('backoffice').one('ruleset', rulesetId).all('info').post('', {name:name, description:description, enabled:enabled});
			}
			service.rulesetDefaultAction = function(rulesetId, action) {
				return accessRulesService.one('backoffice').one('ruleset', rulesetId).all('default-action').post('', {action: action});
			}
			service.changeRuleOrderUp = function(rulesetId, type, ruleId) {
				return accessRulesService.one('backoffice').one('ruleset', rulesetId).one('rule', ruleId).all('orderup').post('', {type: type});
			}
			service.changeRuleOrderDown = function(rulesetId, type, ruleId) {
				return accessRulesService.one('backoffice').one('ruleset', rulesetId).one('rule', ruleId).all('orderdown').post('', {type: type});
			}
			service.addRule = function(rulesetId, type, providerUrl, listId, listName, actionFailed, actionSuccess, ipResetTime, validateOnce, enabled, outcomes) {
				return accessRulesService.one('backoffice').one('ruleset', rulesetId).all('rule-add').post({
					type: type,
					providerUrl: providerUrl,
					listId: listId,
					name: listName,
					actionFailed: actionFailed,
					actionSuccess: actionSuccess,
					ipResetTime: ipResetTime,
					validateOnce: validateOnce,
					enabled: enabled,
					outcomes: outcomes
				});
			}
			service.editRule = function(rulesetId, type, ruleId, name, actionFailed, actionSuccess, ipResetTime, validateOnce, enabled, outcomes) {
				return accessRulesService.one('backoffice').one('ruleset', rulesetId).one('rule', ruleId).all('edit').post({
					type: type,
					actionFailed: actionFailed,
					actionSuccess: actionSuccess,
					ipResetTime: ipResetTime,
					validateOnce: validateOnce,
					enabled: enabled,
					name: name,
					outcomes: outcomes
				});
			}

			service.deleteRule = function(rulesetId, ruleId, type) {
				return accessRulesService.one('backoffice').one('ruleset', rulesetId).one('rule', ruleId).all('delete').post('', {
					type: type
				});
			}

			service.ruleHasTranData = function(rulesetId, ruleId, type) {
				return accessRulesService.all('backoffice').one('ruleset', rulesetId).one('rule', ruleId).one('has-tran-data').get({ type: type });
			}

			service.saveRuleMessage = function(rulesetId, ruleId, message) {
				return accessRulesService.one('backoffice').one('ruleset', rulesetId).one('rule', ruleId).all('update-messages').post(message);
			}

			service.changelogs = function(domainName, rulesetId, page) {
				return accessRulesService.one('backoffice').one('ruleset', rulesetId).one('changelogs').get({ p: page });
			}

			service.eligableLists = function(rulesetId) {
				return accessRulesService.one('backoffice').one('ruleset', rulesetId).all('eligable-lists').getList();
			}

			service.findByName = function(domainName, accessRuleName) {
				return accessRulesService.one('accessrules').one(domainName).one(accessRuleName).get();
			}

			service.add = function(domainName, accessRuleName) {
				return accessRulesService.one('accessrules', domainName).one('create', accessRuleName).post();
			}

			service.findByDomainName = function(domainName) {
				return accessRulesService.one('accessrules').one('findByDomain', domainName).getList();
			}
			
			service.getStatusOptionOutcomeList =  function() {
				return accessRulesService.one('accessrules').all('getStatusOptionOutcomeList').getList();
			}

			service.getStatusOptionOutputList =  function() {
				return accessRulesService.one('accessrules').all('getStatusOptionOutputList').getList();
			}

			return service;
		} catch (error) {
			$log.error(error);
			throw error;
		}
	}
]);

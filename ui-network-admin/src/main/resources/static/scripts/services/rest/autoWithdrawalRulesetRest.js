'use strict';

angular.module('lithium').factory('AutoWithdrawalRulesetRest', ['Restangular',
    function(Restangular) {
        try {
            var service = {};
            var config = Restangular.withConfig(function(RestangularConfigurer) {
                RestangularConfigurer.setBaseUrl('services/service-cashier/admin/auto-withdrawal/ruleset/');
            });

            service.fields = function() {
                return config.all("rule").all("fields").getList();
            }

            service.operators = function() {
                return config.all("rule").all("operators").getList();
            }
            service.autoWithdrawalRuleInitData = function(domainName, typeId) {
                return config.all("rule").all(domainName).all('init-data').one(typeId.toString()).get();
            }

            service.autoWithdrawalRulesetById = function(rulesetId) {
                return config.one(rulesetId).get();
            }

            service.autoWithdrawalRulesetCreate = function(domainName, ruleset) {
                return config.all(""+domainName).all("create").post(ruleset);
            }

            service.autoWithdrawalRulesetChangeName = function(domainName, rulesetId, newName) {
                return config.all(""+domainName).one(rulesetId+"").all("changename").customPOST('', '', {newName: newName}, {});
            }

            service.autoWithdrawalRulesetChangeDelay = function(domainName, rulesetId, newDelay, delayedStart) {
                return config.all(""+domainName).one(rulesetId+"").all("change-delay").customPOST('', '', {newDelay: newDelay, delayedStart: delayedStart}, {});
            }

            service.autoWithdrawalRulesetDelete = function(domainName, rulesetId) {
                return config.all(""+domainName).one(rulesetId+"").all("delete").post();
            }

            service.autoWithdrawalRulesetToggleEnabled = function(domainName, rulesetId) {
                return config.all(""+domainName).one(rulesetId+"").all("toggle").all("enabled").post();
            }

            service.autoWithdrawalRulesetRuleAdd = function(domainName, rulesetId, rule) {
                return config.all(""+domainName).one(rulesetId+"").all("rule").all("add").post(rule);
            }

            service.autoWithdrawalRulesetRuleUpdate = function(domainName, rulesetId, ruleId, ruleUpdate) {
                return config.all(""+domainName).one(rulesetId+"").one("rule", ruleId).all("update").post(ruleUpdate);
            }

            service.autoWithdrawalRulesetRuleDelete = function(domainName, rulesetId, ruleId) {
                return config.all(""+domainName).one(rulesetId+"").one("rule", ruleId).all("delete").post();
            }

            service.queueAutoWithdrawalRulesetProcess = function(domainName, rulesetId) {
                return config.all(""+domainName).one(rulesetId+"").all("queueprocess").post();
            }

            service.changelogs = function(domainName, entityId, page) {
                return config.one(entityId+"").one('changelogs').get({ p: page });
            }

            return service;
        } catch (err) {
            console.error(err);
            throw err;
        }
    }
]);

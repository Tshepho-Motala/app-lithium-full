'use strict';

'use strict';

angular.module('lithium').factory('AutoRestrictionRulesetRest', ['Restangular',
    function(Restangular) {
        try {
            var service = {};
            var config = Restangular.withConfig(function(RestangularConfigurer) {
                RestangularConfigurer.setBaseUrl('services/service-limit/backoffice/auto-restriction/');
            });

            service.fields = function() {
                return config.all('helper').all("rule").all("fields").getList();
            }

            service.operators = function() {
                return config.all('helper').all("rule").all("operators").getList();
            }

            service.outcomes = function() {
                return config.all('helper').all("outcomes").getList();
            }

            service.restrictionEvents = function() {
                return config.all('helper').all("events").getList();
            }

            service.autoRestrictionRulesetById = function(rulesetId) {
                return config.one('rulesets', rulesetId).get();
            }

            service.autoRestrictionRulesetCreate = function(ruleset) {
                return config.all('ruleset').all(ruleset.domain.name).all("create").post(ruleset);
            }

            service.autoRestrictionRulesetChangeName = function(domainName, rulesetId, newName) {
                return config.all('ruleset').all(domainName).all(rulesetId).all("changename").customPOST('', '', {newName: newName}, {});
            }

            service.autoRestrictionRulesetChangeOutcome = function(domainName, rulesetId, newOutcome) {
                return config.all('ruleset').all(domainName).all(rulesetId).all("changeoutcome").customPOST('', '', {newOutcome: newOutcome}, {});
            }

            service.autoRestrictionRulesetSkipTestUser = function(domainName, rulesetId, newSkipTestUser) {
                return config.all('ruleset').all(domainName).all(rulesetId).all("set-skip-test-user").customPOST('', '', {newSkipTestUser: newSkipTestUser}, {});
            }

            service.autoRestrictionRulesetChangeRestriction = function(domainName, rulesetId, newRestrictionId) {
                return config.all('ruleset').all(domainName).all(rulesetId).all("changerestriction").customPOST('', '', {newRestrictionId: newRestrictionId}, {});
            }

            service.autoRestrictionRulesetDelete = function(domainName, rulesetId) {
                return config.all('ruleset').all(domainName).all(rulesetId).all("delete").post();
            }

            service.autoRestrictionRulesetToggleEnabled = function(domainName, rulesetId) {
                return config.all('ruleset').all(domainName).all(rulesetId).all("toggle").all("enabled").post();
            }

            service.autoRestrictionRulesetRuleAdd = function(domainName, rulesetId, rule) {
                return config.all('ruleset').all(domainName).all(rulesetId).all("rule").all("add").post(rule);
            }

            service.autoRestrictionRulesetRuleUpdate = function(domainName, rulesetId, ruleId, ruleUpdate) {
                return config.all('ruleset').all(domainName).all(rulesetId).one("rule", ruleId).all("update").post(ruleUpdate);
            }

            service.autoRestrictionRulesetRuleDelete = function(domainName, rulesetId, ruleId) {
                return config.all('ruleset').all(domainName).all(rulesetId).one("rule", ruleId).all("delete").post();
            }

            service.changelogs = function(domainName, entityId, page) {
                return config.all('ruleset').all(domainName).all(entityId).one('changelogs').get({ p: page });
            }

            return service;
        } catch (err) {
            console.error(err);
            throw err;
        }
    }
]);
